import Vue from 'vue'
import Router from 'vue-router'
import user from './views/user.vue'

Vue.use(Router);

export default new Router({
    mode: "history",
    routes: [
        {
            path: '/',
            name: 'user',
            component: user
        }
    ]
})