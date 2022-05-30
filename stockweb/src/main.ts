import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import ElementUI from 'element-plus'
import 'element-plus/dist/index.css'
import axios from "axios";

axios.defaults.baseURL = 'http://47.108.14.103:8001'
const app = createApp(App)
app.use(ElementUI)
app.use(store).use(router).mount("#app")
