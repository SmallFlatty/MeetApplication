import { createRouter, createWebHistory } from 'vue-router'

//Main Menu
const MainMenu = () => import( '@/Components/Views/MainPage/MainMenuView.vue')

//General Pages
const Profile = () => import('@/Components/Views/GeneralPages/ProfileView.vue')
const Support= () => import('@/Components/Views/GeneralPages/SupportView.vue')
const Report  = () => import('@/Components/Views/GeneralPages/ProblemReportView.vue')

//User Tools
const Users = () => import("@/Components/Views/UserTools/UsersView.vue")
const UserRegister = () => import("@/Components/Views/UserTools/UserRegisterView.vue")
const MyMeeting = () => import("@/Components/Views/UserTools/MyMeetingView.vue")
const AllMeets = () => import("@/Components/Views/UserTools/MeetingsAllView.vue")
const CreateMeet = () => import("@/Components/Views/UserTools/MeetingCreateView.vue")
const CreateMeetForWork = () => import("@/Components/Views/UserTools/MeetingCreateForWorkerView.vue")

const routes = [
    //Main Menu
    {path: '/',name: 'home' , component: MainMenu},

    //General Pages
    {path: '/profile', name: 'profile', component: Profile},
    {path: '/support', name: 'support', component: Support},
    {path: '/report', name: 'report', component: Report},

    //User Tools
    {path: '/users' , name: 'users', component: Users },
    {path: '/user/register' , name: 'user.register' , component: UserRegister},
    {path: '/meetings/mine' , name: 'meetings.mine' , component: MyMeeting },
    {path: '/meetings/all' , name: 'meetings.all' , component: AllMeets },
    {path: '/meetings/create' , name: 'meetings.create', component: CreateMeet },
    {path: '/meetings/create-worker' , name: 'meetings.create.worker', component: CreateMeetForWork},


]

export const router = createRouter({
    history: createWebHistory(),
    routes
})
