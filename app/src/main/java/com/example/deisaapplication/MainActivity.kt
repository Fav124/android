package com.example.deisaapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.deisaapplication.data.local.SessionManager
import com.example.deisaapplication.data.remote.RetrofitClient
import com.example.deisaapplication.ui.components.DeisaDivider
import com.example.deisaapplication.ui.screens.admin.AdminManagementScreen
import com.example.deisaapplication.ui.screens.admin.AdminManagementViewModel
import com.example.deisaapplication.ui.screens.auth.AuthViewModel
import com.example.deisaapplication.ui.screens.auth.LoginScreen
import com.example.deisaapplication.ui.screens.splash.SplashScreen
import com.example.deisaapplication.ui.screens.bed.InfirmaryBedScreen
import com.example.deisaapplication.ui.screens.bed.InfirmaryBedViewModel
import com.example.deisaapplication.ui.screens.dashboard.DashboardScreen
import com.example.deisaapplication.ui.screens.dashboard.DashboardViewModel
import com.example.deisaapplication.ui.screens.settings.SettingsScreen
import com.example.deisaapplication.ui.screens.settings.SettingsViewModel
import com.example.deisaapplication.ui.screens.master.MasterDataScreen
import com.example.deisaapplication.ui.screens.master.MasterDataViewModel
import com.example.deisaapplication.ui.screens.master.MasterSection
import com.example.deisaapplication.ui.screens.medicine.MedicineDetailScreen
import com.example.deisaapplication.ui.screens.medicine.MedicineFormScreen
import com.example.deisaapplication.ui.screens.medicine.MedicineScreen
import com.example.deisaapplication.ui.screens.medicine.MedicineViewModel
import com.example.deisaapplication.ui.screens.referral.HospitalReferralDetailScreen
import com.example.deisaapplication.ui.screens.referral.HospitalReferralFormScreen
import com.example.deisaapplication.ui.screens.referral.HospitalReferralScreen
import com.example.deisaapplication.ui.screens.referral.HospitalReferralViewModel
import com.example.deisaapplication.ui.screens.report.ReportScreen
import com.example.deisaapplication.ui.screens.report.ReportViewModel
import com.example.deisaapplication.ui.screens.santri.SantriDetailScreen
import com.example.deisaapplication.ui.screens.santri.SantriFormScreen
import com.example.deisaapplication.ui.screens.santri.SantriScreen
import com.example.deisaapplication.ui.screens.santri.SantriViewModel
import com.example.deisaapplication.ui.screens.sickness.SicknessCaseDetailScreen
import com.example.deisaapplication.ui.screens.sickness.SicknessCaseFormScreen
import com.example.deisaapplication.ui.screens.sickness.SicknessCaseScreen
import com.example.deisaapplication.ui.screens.sickness.SicknessCaseViewModel
import com.example.deisaapplication.ui.theme.AppBackground
import com.example.deisaapplication.ui.theme.AppError
import com.example.deisaapplication.ui.theme.AppSurface
import com.example.deisaapplication.ui.theme.DeisaApplicationTheme
import com.example.deisaapplication.ui.theme.MutedText
import com.example.deisaapplication.ui.theme.OnAppBackground
import com.example.deisaapplication.ui.theme.Primary
import kotlinx.coroutines.launch

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"

    const val DASHBOARD = "dashboard"
    const val SANTRI = "santri"
    const val CLASS = "class"
    const val MAJOR = "major"
    const val DORMITORY = "dormitory"
    const val SICKNESS = "sickness"
    const val MEDICINE = "medicine"
    const val BED = "bed"
    const val REFERRAL = "referral"
    const val REPORT = "report"
    const val USERS = "users"
    const val APPROVALS = "approvals"
    const val ADMIN_MANAGEMENT = "admin_management"

    const val SANTRI_DETAIL = "santri/{id}"
    const val SANTRI_FORM = "santri_form?id={id}"
    const val SICKNESS_DETAIL = "sickness/{id}"
    const val SICKNESS_FORM = "sickness_form?id={id}"
    const val MEDICINE_DETAIL = "medicine/{id}"
    const val MEDICINE_FORM = "medicine_form?id={id}"
    const val REFERRAL_DETAIL = "referral/{id}"
    const val REFERRAL_FORM = "referral_form?id={id}"
    const val SETTINGS = "settings"

    fun withId(pattern: String, id: Int) = pattern.replace("{id}", id.toString())
}

data class DrawerNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val visible: Boolean = true,
)

class MainActivity : ComponentActivity() {
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        session = SessionManager(applicationContext)
        RetrofitClient.init(session.getPrefs())

        setContent {
            DeisaApplicationTheme {
                DeisaApp(session)
            }
        }
    }
}

@Composable
fun DeisaApp(session: SessionManager) {
    val rootNav = rememberNavController()
    val startDestination = Routes.SPLASH
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory(session))

    NavHost(navController = rootNav, startDestination = startDestination) {
        composable(Routes.SPLASH) {
            SplashScreen(onAnimationFinished = {
                val nextRoute = if (session.isLoggedIn()) Routes.MAIN else Routes.LOGIN
                rootNav.navigate(nextRoute) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    rootNav.navigate(Routes.MAIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    rootNav.navigate(Routes.REGISTER)
                }
            )
        }
        composable(Routes.REGISTER) {
            com.example.deisaapplication.ui.screens.auth.RegisterScreen(
                viewModel = authViewModel,
                onBackToLogin = {
                    rootNav.popBackStack()
                },
                onRegisterSuccess = {
                    rootNav.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MAIN) {
            MainShell(session = session) {
                rootNav.navigate(Routes.LOGIN) {
                    popUpTo(Routes.MAIN) { inclusive = true }
                }
            }
        }
    }
}

@Composable
private fun MainShell(session: SessionManager, onLogout: () -> Unit) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val canManageData = session.canManageData()
    val canAccessHealth = session.canAccessHealth()
    val isSuperAdmin = session.isSuperAdmin()
    val currentUser = session.getUser()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val mainItems = listOf(
        DrawerNavItem(Routes.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
    )
    val masterItems = listOf(
        DrawerNavItem(Routes.SANTRI, "Data Santri", Icons.Default.People, visible = canAccessHealth),
        DrawerNavItem(Routes.CLASS, "Data Kelas", Icons.Default.Class, visible = canManageData),
        DrawerNavItem(Routes.MAJOR, "Data Jurusan", Icons.Default.School, visible = canManageData),
        DrawerNavItem(Routes.DORMITORY, "Data Asrama", Icons.Default.Apartment, visible = canManageData),
    )
    val healthItems = listOf(
        DrawerNavItem(Routes.MEDICINE, "Stok Obat", Icons.Default.Medication, visible = canAccessHealth),
        DrawerNavItem(Routes.BED, "Kasur UKS", Icons.Default.Bed, visible = canAccessHealth),
        DrawerNavItem(Routes.SICKNESS, "Santri Sakit", Icons.Default.MedicalServices, visible = canAccessHealth),
        DrawerNavItem(Routes.REFERRAL, "Rujukan RS", Icons.Default.LocalHospital, visible = canAccessHealth),
        DrawerNavItem(Routes.REPORT, "Laporan", Icons.Default.Assessment, visible = canAccessHealth),
    )
    val adminItems = listOf(
        DrawerNavItem(Routes.ADMIN_MANAGEMENT, "Manajemen User", Icons.Default.AdminPanelSettings, visible = isSuperAdmin),
        DrawerNavItem(Routes.SETTINGS, "Pengaturan", Icons.Default.Settings),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = AppSurface,
                drawerContentColor = OnAppBackground,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                        Box(
                            modifier = Modifier
                                .size(60.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.dei),
                                contentDescription = "Logo",
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("DEIHealth", color = OnAppBackground, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text(currentUser?.name ?: "Pengguna", color = OnAppBackground, fontWeight = FontWeight.SemiBold)
                        Text(currentUser?.roleLabel ?: "", color = MutedText, fontSize = 12.sp)
                    }
                    DrawerSection("Navigasi Utama", mainItems, currentRoute, navController) { scope.launch { drawerState.close() } }
                    if (masterItems.any { it.visible }) {
                        DrawerSection("Master Data", masterItems, currentRoute, navController) { scope.launch { drawerState.close() } }
                    }
                    if (healthItems.any { it.visible }) {
                        DrawerSection("Modul Kesehatan", healthItems, currentRoute, navController) { scope.launch { drawerState.close() } }
                    }
                    if (adminItems.any { it.visible }) {
                        DrawerSection("Administrasi", adminItems, currentRoute, navController) { scope.launch { drawerState.close() } }
                    }
                    Spacer(Modifier.weight(1f))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    NavigationDrawerItem(
                        label = { Text("Keluar Aplikasi") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            session.clear()
                            onLogout()
                        },
                        icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = AppSurface,
                            unselectedIconColor = AppError,
                            unselectedTextColor = AppError,
                        ),
                        modifier = Modifier.padding(12.dp),
                    )
                }
            }
        },
    ) {
        Scaffold(containerColor = AppBackground) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Routes.DASHBOARD,
                modifier = Modifier.padding(paddingValues),
            ) {
                composable(Routes.DASHBOARD) {
                    val vm: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory())
                    DashboardScreen(
                        viewModel = vm,
                        userName = currentUser?.name ?: "Pengurus",
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onNavigateSicknessCase = { navController.navigate(Routes.SICKNESS) },
                        onNavigateMedicine = { navController.navigate(Routes.MEDICINE) },
                        onNavigateReferral = { navController.navigate(Routes.REFERRAL) },
                    )
                }
                composable(Routes.SANTRI) {
                    val vm: SantriViewModel = viewModel(factory = SantriViewModel.Factory())
                    SantriScreen(
                        viewModel = vm,
                        canManageData = canManageData,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onAddNew = { navController.navigate(Routes.SANTRI_FORM.replace("{id}", "-1")) },
                        onViewDetail = { navController.navigate(Routes.withId(Routes.SANTRI_DETAIL, it)) },
                    )
                }
                composable(Routes.CLASS) {
                    val vm: MasterDataViewModel = viewModel(factory = MasterDataViewModel.Factory())
                    MasterDataScreen(
                        section = MasterSection.CLASS,
                        viewModel = vm,
                        canManageData = canManageData,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                    )
                }
                composable(Routes.MAJOR) {
                    val vm: MasterDataViewModel = viewModel(factory = MasterDataViewModel.Factory())
                    MasterDataScreen(
                        section = MasterSection.MAJOR,
                        viewModel = vm,
                        canManageData = canManageData,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                    )
                }
                composable(Routes.DORMITORY) {
                    val vm: MasterDataViewModel = viewModel(factory = MasterDataViewModel.Factory())
                    MasterDataScreen(
                        section = MasterSection.DORMITORY,
                        viewModel = vm,
                        canManageData = canManageData,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                    )
                }
                composable(Routes.SICKNESS) {
                    val vm: SicknessCaseViewModel = viewModel(factory = SicknessCaseViewModel.Factory())
                    SicknessCaseScreen(
                        viewModel = vm,
                        canManageData = canManageData,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onAddNew = { navController.navigate(Routes.SICKNESS_FORM.replace("{id}", "-1")) },
                        onViewDetail = { navController.navigate(Routes.withId(Routes.SICKNESS_DETAIL, it)) },
                    )
                }
                composable(Routes.MEDICINE) {
                    val vm: MedicineViewModel = viewModel(factory = MedicineViewModel.Factory())
                    MedicineScreen(
                        viewModel = vm,
                        canManageData = canManageData,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onAddNew = { navController.navigate(Routes.MEDICINE_FORM.replace("{id}", "-1")) },
                        onViewDetail = { navController.navigate(Routes.withId(Routes.MEDICINE_DETAIL, it)) },
                    )
                }
                composable(Routes.BED) {
                    val vm: InfirmaryBedViewModel = viewModel(factory = InfirmaryBedViewModel.Factory())
                    InfirmaryBedScreen(
                        viewModel = vm,
                        canManageData = canManageData,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onAddNew = {},
                    )
                }
                composable(Routes.REFERRAL) {
                    val vm: HospitalReferralViewModel = viewModel(factory = HospitalReferralViewModel.Factory())
                    HospitalReferralScreen(
                        viewModel = vm,
                        canManageData = canManageData,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                        onAddNew = { navController.navigate(Routes.REFERRAL_FORM.replace("{id}", "-1")) },
                        onViewDetail = { navController.navigate(Routes.withId(Routes.REFERRAL_DETAIL, it)) },
                    )
                }
                composable(Routes.REPORT) {
                    val vm: ReportViewModel = viewModel(factory = ReportViewModel.Factory())
                    ReportScreen(viewModel = vm, onOpenDrawer = { scope.launch { drawerState.open() } })
                }
                composable(Routes.ADMIN_MANAGEMENT) {
                    val vm: AdminManagementViewModel = viewModel(factory = AdminManagementViewModel.Factory())
                    AdminManagementScreen(
                        title = "Manajemen User",
                        defaultStatus = null,
                        viewModel = vm,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                    )
                }
                composable(Routes.SETTINGS) {
                    val vm: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory(session))
                    SettingsScreen(
                        viewModel = vm,
                        onOpenDrawer = { scope.launch { drawerState.open() } },
                    )
                }

                composable(
                    route = Routes.SANTRI_DETAIL,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id") ?: 0
                    val vm: SantriViewModel = viewModel(factory = SantriViewModel.Factory())
                    SantriDetailScreen(
                        id = id,
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Routes.withId(Routes.SANTRI_FORM, it)) },
                    )
                }
                composable(
                    route = Routes.SICKNESS_DETAIL,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id") ?: 0
                    val vm: SicknessCaseViewModel = viewModel(factory = SicknessCaseViewModel.Factory())
                    SicknessCaseDetailScreen(
                        id = id,
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Routes.withId(Routes.SICKNESS_FORM, it)) },
                    )
                }
                composable(
                    route = Routes.MEDICINE_DETAIL,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id") ?: 0
                    val vm: MedicineViewModel = viewModel(factory = MedicineViewModel.Factory())
                    MedicineDetailScreen(
                        id = id,
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Routes.withId(Routes.MEDICINE_FORM, it)) },
                    )
                }
                composable(
                    route = Routes.REFERRAL_DETAIL,
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id") ?: 0
                    val vm: HospitalReferralViewModel = viewModel(factory = HospitalReferralViewModel.Factory())
                    HospitalReferralDetailScreen(
                        id = id,
                        viewModel = vm,
                        onBack = { navController.popBackStack() },
                        onEdit = { navController.navigate(Routes.withId(Routes.REFERRAL_FORM, it)) },
                    )
                }
                composable(
                    route = Routes.SANTRI_FORM,
                    arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 }),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id").takeIf { it != -1 }
                    val vm: SantriViewModel = viewModel(factory = SantriViewModel.Factory())
                    SantriFormScreen(id = id, viewModel = vm, onBack = { navController.popBackStack() })
                }
                composable(
                    route = Routes.SICKNESS_FORM,
                    arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 }),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id").takeIf { it != -1 }
                    val vm: SicknessCaseViewModel = viewModel(factory = SicknessCaseViewModel.Factory())
                    SicknessCaseFormScreen(id = id, viewModel = vm, onBack = { navController.popBackStack() })
                }
                composable(
                    route = Routes.MEDICINE_FORM,
                    arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 }),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id").takeIf { it != -1 }
                    val vm: MedicineViewModel = viewModel(factory = MedicineViewModel.Factory())
                    MedicineFormScreen(id = id, viewModel = vm, onBack = { navController.popBackStack() })
                }
                composable(
                    route = Routes.REFERRAL_FORM,
                    arguments = listOf(navArgument("id") { type = NavType.IntType; defaultValue = -1 }),
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id").takeIf { it != -1 }
                    val vm: HospitalReferralViewModel = viewModel(factory = HospitalReferralViewModel.Factory())
                    HospitalReferralFormScreen(id = id, viewModel = vm, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
private fun DrawerSection(
    title: String,
    items: List<DrawerNavItem>,
    currentRoute: String?,
    navController: androidx.navigation.NavHostController,
    onNavigate: () -> Unit,
) {
    val visibleItems = items.filter { it.visible }
    if (visibleItems.isEmpty()) return

    Text(
        text = title,
        color = MutedText,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
    )
    visibleItems.forEach { item ->
        NavigationDrawerItem(
            label = { Text(item.label) },
            selected = currentRoute == item.route,
            onClick = {
                onNavigate()
                navController.navigate(item.route) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Icon(item.icon, contentDescription = null) },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Primary.copy(alpha = 0.14f),
                selectedIconColor = Primary,
                selectedTextColor = OnAppBackground,
                unselectedContainerColor = AppSurface,
                unselectedIconColor = MutedText,
                unselectedTextColor = OnAppBackground,
            ),
            modifier = Modifier.padding(horizontal = 12.dp),
        )
    }
    DeisaDivider()
}
