<template>
  <div id="app">
    <el-container>
      <el-header>
      <el-menu
        class="el-menu-demo"
        mode="horizontal"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item disabled :style="{margin:'auto'}">股票数据展示中心</el-menu-item>
      </el-menu>
      </el-header>

      <el-main>
        <router-view></router-view>
      </el-main>

      <el-footer>
        版权所有ZTY 2022 SWPU
      </el-footer>

    </el-container>
  </div>
</template>

<script lang="ts" setup>

</script>

<style>
.el-header, .el-footer {
  background-color: #545c64;
  color: #333;
  text-align: center;
  line-height: 60px;
  padding: 0;
}
</style>
