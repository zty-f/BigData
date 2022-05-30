<template>
  <el-row>
    <el-col :span="7">
      <el-row>
        <el-col :span="20">
          <el-input v-model="input" placeholder="请输入股票代码进行查询"></el-input>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="search()">搜索</el-button>
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
      <div id="main" style="width: 100%;height: 550px;"></div>
      <el-col>
        <el-table :data="everyDayData" style="width: 100%">
          <el-table-column prop="code" label="股票代码" width="100px"/>
          <el-table-column prop="open" label="开盘价" />
          <el-table-column prop="close" label="收盘价" />
          <el-table-column prop="high" label="最高价" />
          <el-table-column prop="low" label="最低价" />
          <el-table-column prop="volume" label="交易量(手)" width="100px"/>
          <el-table-column prop="money" label="交易量(元)" width="1300px"/>
          <el-table-column prop="paused" label="是否停牌" />
          <el-table-column prop="st" label="是否st" />
        </el-table>
      </el-col>
    </el-col>
  </el-row>
</template>

<script lang="ts" setup>

import {reactive, ref} from 'vue';
import axios from "axios";
import {ElMessage} from 'element-plus';
import * as echarts from 'echarts';
import {ECharts} from "echarts";

//声明Echarts
const input = ref('')
const keyword=ref("6006");


const stocks = reactive([{
  code:'',
  name:''
}])
const prices = reactive([{
  date:'',
  price:''
}])



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
      ElMessage.success("获取股票列表信息成功！");
      console.log(stocks);
    } else {
      ElMessage.error(data.msg);
    }
  });
  return;
}
getStockList();

const search = () => {
  console.log("正在搜索"+input.value+"相关股票信息·····")
  keyword.value = input.value;
  getStockList();
  return;
}

const selectOne = (code: String,name:String) => {
  console.log("正在搜索"+code+"股票价格信息·····")
  getStockPrice(code,name);
  return;
}


const getStockPrice = (code: String,name:String) => {
  console.log('获取股票价格信息······');
  axios.get("/GetStockPrice?code="+code).then((response) => {
    const data = response.data;
    if (data.state === "SUC") {
      prices.length = 0;
      for (let i = 0; i < data.content.length; i++) {
        prices.push(data.content[i]);
      }
      console.log(prices);
      ElMessage.success("获取股票价格信息成功！");
      render(code,name);
    } else {
      ElMessage.error(data.msg);
    }
  });
  return;
}

const render = (code: String,name:String) => {

  const myChart = echarts.init(document.getElementById("main") as HTMLElement);

  // 指定图表的配置项和数据
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    toolbox: {
      feature: {
        saveAsImage: {}
      }
    },
    title: {
      left:'center',
      text: name+'每日股票价格数据图表'
    },
    xAxis: {
      name:'日期',
      data: [{}]
    },
    yAxis: {
      name:'股票价格/元'
    },
    series: [
      { name: '销售价格', type: 'line', data: [{}],smooth: true},    //volume数组放入series[0].data
    ]
  };
  for (let i = 0; i < prices.length; i++) {
    option.xAxis.data[i] = prices[i].date;
    option.series[0].data[i] = parseFloat(prices[i].price);
  }
  // 使用刚指定的配置项和数据显示图表。
  myChart.setOption(option,true);
  //设置图表点击交互事件
  myChart.on("click", function (params) {
    console.log("点击图表······");
    console.log("当前股票代码为："+code);
    console.log("当前选中日期为"+prices[params.dataIndex].date);
    getStockData(code,prices[params.dataIndex].date);
  })
  return;
}



const getStockData = (curCode:String,curDate:String) => {
  console.log('获取股票每日详细信息············');
  axios.get("/GetStockData?code="+curCode+"&date="+curDate).then((response) => {
    const data = response.data;
    if (data.state == "SUC") {
      everyDayData[0].code = data.content.code;
      everyDayData[0].volume = data.content.volume;
      everyDayData[0].low = data.content.low;
      everyDayData[0].high = data.content.high;
      everyDayData[0].st = data.content.st;
      everyDayData[0].close = data.content.close;
      everyDayData[0].money = data.content.money;
      everyDayData[0].paused = data.content.paused;
      everyDayData[0].open = data.content.open;
      console.log(everyDayData);
      ElMessage.success("获取股票每日详细信息成功！");
    } else {
      ElMessage.error(data.msg);
    }
  });
  return;
}

const load = () => {
  stocks.length += 2;
}

</script>


<style>
.infinite-list-wrapper {
  height: 600px;
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
