<template>
  <div id="app">
    <el-container>
      <el-header height="65px">
      <el-menu
        class="el-menu-demo"
        mode="horizontal"
        background-color="#545c64"
        text-color="black"
        active-text-color="#ffd04b"
        default-active = "home"
      >
        <el-menu-item index="home"  :style="{margin:'auto'}" >股票数据展示中心</el-menu-item>
      </el-menu>
      </el-header>

      <el-main>
        <router-view></router-view>
      </el-main>

      <el-footer>
        版权所有©2022 SWPU ZTY(201931104202)
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
