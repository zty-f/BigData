<template>
  <el-row>
    <el-col :span="7">
      <el-row>
        <el-col :span="20">
          <el-input v-model="input" placeholder="请输入股票代码进行查询" style="height: 40px"></el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="search()" style="height: 38px;width: 80px">搜索</el-button>
        </el-col>
      </el-row>
      <div class="infinite-list-wrapper" style="overflow: auto">
        <ul
          v-infinite-scroll="load"
          class="list"
          infinite-scroll-disabled="disabled"
        >
          <li v-for="i in stocks.length" :key="i" class="list-item">
            <el-link style="color: red" @click="selectOne(stocks[i-1].code,stocks[i-1].name)" >
              {{"[股票代码："+stocks[i-1].code+"]~~~"+"[股票名称："+stocks[i-1].name+"]"}}
            </el-link>
          </li>
        </ul>
      </div>
    </el-col>
    <el-col :span="17">
      <el-row>
        <div id="main" style="width: 100%;height: 490px;"></div>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-row v-show="isShowTable">
            <div id="detail" style="font-weight:bold;color: rgba(234,15,15,0.75);font-family:'微软雅黑';text-align: center;width: 100%"></div>
          </el-row>
          <div>
            <el-table
              :data="everyDayData"
              v-show="isShowTable"
              :cell-style="cellStyle"
              :header-row-style="headerRowStyle"
            >
              <el-table-column prop="code" label="股票代码"/>
              <el-table-column prop="open" label="开盘价" style="color: red"/>
              <el-table-column prop="close" label="收盘价" style="color: red"/>
              <el-table-column prop="high" label="最高价" style="color: red"/>
              <el-table-column prop="low" label="最低价" style="color: red"/>
              <el-table-column prop="volume" label="交易量(手)"/>
              <el-table-column prop="money" label="交易量(元)"/>
              <el-table-column prop="paused" label="是否停牌"/>
              <el-table-column prop="st" label="是否st"/>
            </el-table>
          </div>
        </el-col>
      </el-row>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>

import {reactive, ref} from 'vue';
import axios from "axios";
import {ElMessage} from 'element-plus';
import * as echarts from 'echarts';

//输入数据交互变量
const input = ref('')
//默认值
const keyword=ref("6006");
//是否显示详细数据表格标志位
const isShowTable=ref(false);

//股票列表存储
const stocks = reactive([{
  code:'',
  name:''
}])

//股票每日价格存储
const prices = reactive([{
  date:'',
  price:''
}])

// 股票当日详细数据信息存储
const everyDayData = reactive([{
  close:'',
  code:'',
  high: '',
  low: '',
  money: '',
  open: '',
  paused: '',
  st: '',
  volume: ''
}])

//获取keyword开头的股票代码对应的股票列表
const getStockList = () => {
  console.log('获取股票列表信息·········');
  axios.get("/GetStockList?keyword="+keyword.value).then((response) => {
    const data = response.data;
    if (data.state === "SUC") {
      stocks.length = 0;
      for (let i = 0; i < data.content.length; i++) {
          stocks.push(data.content[i]);
      }
      console.log("查询结果数量"+stocks.length);
      // stocks.push(data.content);
      ElMessage.success("获取以["+keyword.value+"]为首的股票代码对应的股票列表信息成功！");
      console.log(stocks);
    } else {
      ElMessage.error(data.msg);
    }
  });
  return;
}

//初始化时调用一次查询接口
getStockList();

//点击搜索按钮进行该股票价格搜索
const search = () => {
  console.log("正在搜索"+input.value+"相关股票信息·····")
  keyword.value = input.value;
  getStockList();
  return;
}

// 选择一天获取当天股票的详细信息
const selectOne = (code: String,name:String) => {
  console.log("正在搜索"+code+"股票价格信息·····")
  getStockPrice(code,name);
  return;
}

// 获取股票价格信息
const getStockPrice = (code: String,name:String) => {
  console.log('获取股票价格信息······');
  axios.get("/GetStockPrice?code="+code).then((response) => {
    const data = response.data;
    if (data.state === "SUC") {
      isShowTable.value = false;
      prices.length = 0;
      for (let i = 0; i < data.content.length; i++) {
        prices.push(data.content[i]);
      }
      console.log(prices);
      ElMessage.success("获取["+code+"]股票每日价格信息成功！");
      render(code,name);
    } else {
      ElMessage.error(data.msg);
    }
  });
  return;
}

// 将价格信息生成Echarts图表展示出来
const render = (code: String,name:String) => {

  const myChart = echarts.init(document.getElementById("main") as HTMLElement);

  // 指定图表的配置项和数据
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
    },
    toolbox: {
      feature: {
        saveAsImage: {}
      }
    },
    title: {
      left:'center',
      text: '〖'+code+'〗'+name+'每日股票价格数据图表'
    },
    xAxis: {
      name:'[日期]',
      data: [{}],
      axisLine: {
        lineStyle: {
          color: "blue",
        }
      },
      // x轴name颜色
      nameTextStyle: {
        color: "black",
        fontSize: "15px"
      }
    },
    yAxis: {
      show:true,
      name:'[股票价格/元]',
      axisLabel: {
        color: "red",
        fontSize: "10px"
      },
      // y轴name颜色
      nameTextStyle: {
        color: "black",
        fontSize: "15px"
      },
      // y轴轴线颜色
      axisLine: {
        show:true,
        symbol: ['none', 'arrow'],
        lineStyle: {
          color: "red",
          width: 1 //这里是坐标轴的宽度
        }
      }
    },
    series: [
      {
        name: '股票收盘价格',
        type: 'line',
        data: [{}],
        smooth: true,
        itemStyle: {
          color: '#6A5ACD',
          normal: {
            lineStyle: {        // 系列级个性化折线样式
              width: 2,
              type: 'solid',
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0,
                color: '#0000FF'
              }, {
                offset: 1,
                color: '#CD5C5C'
              }]),//线条渐变色
            }
          },
          emphasis: {
            color: '#6A5ACD',
            lineStyle: {        // 系列级个性化折线样式
              width: 2,
              type: 'dotted',
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0,
                color: '#1E90FF'
              }, {
                offset: 1,
                color: '#0000FF'
              }])
            }
          }
        },//线条样式
        areaStyle:{
          normal:{
            //颜色渐变函数 前四个参数分别表示四个位置依次为左、下、右、上
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{

              offset: 0,
              color: 'rgba(80,141,255,0.39)'
            }, {
              offset: .34,
              color: 'rgba(56,155,255,0.25)'
            },{
              offset: 1,
              color: 'rgba(38,197,254,0.00)'
            }])
          }
        }
      },
    ]
  };

  // 给图表填充数据
  for (let i = 0; i < prices.length; i++) {
    option.xAxis.data[i] = prices[i].date;
    option.series[0].data[i] = parseFloat(prices[i].price);
  }
  // 使用刚指定的配置项和数据显示图表。
  myChart.setOption(option,true);

  //图表设置交互事件前先关闭之前的事件
  myChart.getZr().off('click');

  //设置图表点击交互事件
  myChart.getZr().on("click",function (params) {
    console.log("点击图表······");
    let pointInPixel= [params.offsetX, params.offsetY];
    if (myChart.containPixel('grid',pointInPixel)) {
      let x = myChart.convertFromPixel({seriesIndex: 0}, pointInPixel)[0];
      console.log("点击处对应x轴坐标为："+x);
      console.log("点击处对应日期为："+prices[x].date);
      console.log("当前股票代码为："+code);
      getStockData(code,prices[x].date,name);
    }
  })
  return;
}

// 获取选中当天的详细股票信息
const getStockData = (curCode:String,curDate:String,stockName:String) => {
  console.log('获取股票每日详细信息············');
  axios.get("/GetStockData?code="+curCode+"&date="+curDate).then((response) => {
    const data = response.data;
    if (data.state == "SUC") {
      isShowTable.value = true;
      everyDayData[0].code = data.content.code;
      everyDayData[0].volume = data.content.volume;
      everyDayData[0].low = data.content.low;
      everyDayData[0].high = data.content.high;
      everyDayData[0].st = data.content.st;
      everyDayData[0].close = data.content.close;
      everyDayData[0].money = data.content.money;
      everyDayData[0].paused = data.content.paused;
      everyDayData[0].open = data.content.open;
      let element = document.getElementById('detail') as HTMLElement;
      element.innerText = "『"+stockName+"』股票在〔"+curDate+"〕当天的详细信息:";
      console.log(everyDayData);
      ElMessage.success("获取["+curCode+"]股票在["+curDate+"]当天的详细信息成功！");
    } else {
      ElMessage.error(data.msg);
    }
  });
  return;
}

// 详细信息表的单元格样式调用方法
const cellStyle = ({column}:any) => {

  if (column.label === '股票代码') {
    return {
      backgroundColor: 'black',
      color: 'white'
    }
  }

  if (column.label === '开盘价') {
    return {
      backgroundColor: 'rgba(188,241,241,0.75)',
      color: 'red'
    }
  }
  if (column.label === '收盘价') {
    return {
      backgroundColor: 'rgba(188,241,241,0.75)',
      color: 'red'
    }
  }
  if (column.label === '最高价') {
    return {
      backgroundColor: '#64F6F6BF',
      color: 'red'
    }
  }
  if (column.label === '最低价') {
    return {
      backgroundColor: '#64F6F6BF',
      color: 'rgba(23,169,5,0.75)'
    }
  }
  if (column.label === '是否停牌'||column.label === '是否st') {
    return {
      backgroundColor: 'rgba(214,236,236,0.75)',
      color: 'rgba(17,71,171,0.75)'
    }
  }

  if (column.label === '交易量(手)'||column.label === '交易量(元)') {
    return {
      backgroundColor: 'rgba(238,241,238,0.75)',
      color: 'rgba(189,13,238,0.75)'
    }
  }
}

// 详细信息表格表头样式调用方法
const headerRowStyle = () => {
  return {
    color: 'black'
  }
}

// 滚动列表滑动时调用方法
const load = () => {
  stocks.length += 2;
}

</script>


<style>
.infinite-list-wrapper {
  height: 550px;
  text-align: center;
}
.infinite-list-wrapper .list {
  padding: 0;
  margin: 0;
  list-style: none;
}

.infinite-list-wrapper .list-item {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 50px;
  background: var(--el-color-danger-light-9);
  color: var(--el-color-danger);
}
.infinite-list-wrapper .list-item + .list-item {
  margin-top: 10px;
}
</style>
