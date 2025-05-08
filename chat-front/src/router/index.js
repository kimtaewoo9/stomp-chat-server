import { createRouter, createWebHashHistory } from "vue-router"
import MemberCreate from "@/views/MemberCreate.vue"
import LoginPage from "@/views/LoginPage.vue"
import MemberList from "@/views/MemberList.vue"
import SimpleWebsocket from "@/views/SimpleWebsocket.vue"
import StompChatPage from "@/views/StompChatPage.vue"
import GroupChattingList from "@/views/GroupChattingList.vue"
import MyChatPage from "@/views/MyChatPage.vue"

const routes = [
    {
        path:'/member/create',
        name: 'MemberCreate',
        component: MemberCreate
    },
    {
        path:'/login',
        name: 'LoginPage',
        component: LoginPage
    },
    {
        path:'/member/list',
        name: 'MemberList',
        component: MemberList,
        meta: { requiresAuth: true }
    },
    {
        path:'/simple/chat',
        name: 'SimpleWebsocket',
        component: SimpleWebsocket,
        meta: { requiresAuth: true }
    },
    {
        path:'/chatpage/:roomId',
        name: 'StompChatPage',
        component: StompChatPage,
        meta: { requiresAuth: true }
    },
    {
        path:'/groupchatting/list',
        name: 'GroupChattingList',
        component: GroupChattingList,
        meta: { requiresAuth: true }
    },
    {
        path:'/my/chat/page',
        name: 'MyChatPage',
        component: MyChatPage,
        meta: { requiresAuth: true }
    }

]

const router = createRouter({
    history: createWebHashHistory(),
    routes
});

// 네비게이션 가드 추가
router.beforeEach((to, from, next) => {
    // 로그인이 필요한 페이지인지 확인
    if (to.matched.some(record => record.meta.requiresAuth)) {
        // 로그인 상태 확인 (localStorage에서 토큰 확인)
        if (!localStorage.getItem("token")) {
            // 로그인되지 않은 경우
            alert("로그인이 필요한 페이지입니다.");
            next({
                path: '/login',
                query: { redirect: to.fullPath } // 로그인 후 원래 가려던 페이지로 이동하기 위한 정보
            });
        } else {
            // 로그인된 경우 정상적으로 페이지 이동
            next();
        }
    } else {
        // 로그인이 필요없는 페이지는 그냥 통과
        next();
    }
});

export default router;