# hilt-conditional-navigation

Demo showing how to implement [test navigation][1] with [conditional navigation][2].

The demo contains 3 fragments: `HomeFragment`, `ProfileFragment` and `LoginFragment`. In the `ProfileFragment` there is a condition checking if the user is connected or not. If the user is not connected he is sent to the `LoginFragment`.

### ProfileFragment ###

```kotlin
@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val navController by lazy { findNavController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentBackStackEntry = navController.currentBackStackEntry!!
        currentBackStackEntry.savedStateHandle.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry, { success ->
                if (!success) {
                    val startDestination = navController.graph.startDestination
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.user.observe(viewLifecycleOwner, {
            if(!it){
                navController.navigate(R.id.loginFragment)
            }
        })
    }
}
```
As explained in the documentation, in the `onCreate()` method I'm observing the `LOGIN_SUCCESSFUL` value stored in the `SavedStateHandle` in order to redirect the user if he didn't log in.

### LoginFragment ###

```kotlin
@AndroidEntryPoint
class LoginFragment : Fragment() {
    companion object {
        const val LOGIN_SUCCESSFUL: String = "LOGIN_SUCCESSFUL"
    }

    private val viewModel: LoginViewModel by viewModels()
    private val navController by lazy { findNavController() }

    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedStateHandle = navController.previousBackStackEntry!!.savedStateHandle
        savedStateHandle.set(LOGIN_SUCCESSFUL, false)

        viewModel.login.observe(viewLifecycleOwner, ResultObserver {
            if (it.status == Status.ERROR) {
                Snackbar.make(requireView(), it.message ?: "Error", Snackbar.LENGTH_SHORT).show()
            } else if (it.status == Status.SUCCESS) {
                savedStateHandle.set(LOGIN_SUCCESSFUL, true)
                navController.popBackStack()
            }
        })
    }
}
```

### Testing ###

```kotlin
@Test
fun profileFragment_unauthenticated() {
    setAuthentication(false)

    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    launchFragmentInHiltContainer<ProfileFragment> {
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_main)
        navController.setCurrentDestination(R.id.profileFragment)
        Navigation.setViewNavController(requireView(), navController)
    }
    assertEquals(navController.currentDestination?.id, R.id.loginFragment)
}
```
Because I'm using Hilt for dependencies injection I need to attach the fragment to an activity annotated with `@AndroidEntryPoint`: [launchFragmentInHiltContainer][4]

During the test, I'm creating an instance of `TestNavHostController` and assigning it to the fragment.

The problem is that I am accessing the `navController` in the `onCreate` method while the `navController` is instantiated during the `RESUMED` state (see [FragmentScenario][5]). Causing a ` java.lang.IllegalStateException: Fragment ProfileFragment ... does not have a NavController set`

I know it's possibile to make the `navController` available sooner by adding an observer to the fragment's `viewLifecycleOwnerLiveData`. However, it is not soon enough.

So, how to implement test navigation in a such case?

##  Possible solution ##

After several days looking for a way to add test to my fragment I thought I found a solution. However, I'm not totally satisfied with the result, that is why I'm not proposing this solution as an answer.

Because I had problems with the `navController` and the lifecycle of the fragment I decided to attach the `navController` to the activity. 

I updated `HiltTestActivity`, `ProfileFragment` and the `launchFragmentInHiltContainer` function to attach the controller to the activity's view:

### HiltTestActivity ###

```Kotlin
@AndroidEntryPoint
class HiltTestActivity : AppCompatActivity(R.layout.activity_main)
```

### ProfileFragment ###
Instantiate the controller by looking into the activity instead of the fragment:

```kotlin
private val navController by lazy {
    Navigation.findNavController(requireActivity(), R.id.main_nav_host)
}
````

### launchFragmentInHiltContainer ### 

```kotlin
inline fun <reified T : Fragment> launchFragmentInHiltContainer(
    fragmentArgs: Bundle? = null,
    @StyleRes themeResId: Int = R.style.FragmentScenarioEmptyFragmentActivityTheme,
    crossinline action: Activity.() -> Unit = {}
) {
    val startActivityIntent = Intent.makeMainActivity(
        ComponentName(
            ApplicationProvider.getApplicationContext(),
            HiltTestActivity::class.java
        )
    ).putExtra(
        "androidx.fragment.app.testing.FragmentScenario.EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY",
        themeResId
    )

    ActivityScenario.launch<HiltTestActivity>(startActivityIntent).onActivity { activity ->
        val fragment: Fragment = activity.supportFragmentManager.fragmentFactory.instantiate(
            Preconditions.checkNotNull(T::class.java.classLoader),
            T::class.java.name
        )
        activity.action()

        fragment.arguments = fragmentArgs
        activity.supportFragmentManager
            .beginTransaction()
            .add(android.R.id.content, fragment, "")
            .commitNow()
    }
}
```

### Testing ###
Update the view used to set the `navController`:

```kotlin
@Test
fun profileFragment_unauthenticated() {
    setAuthentication(false)

    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    launchFragmentInHiltContainer<ProfileFragment> {
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.nav_main)
        navController.setCurrentDestination(R.id.profileFragment)
        Navigation.setViewNavController(findViewById(R.id.main_nav_host), navController)
    }

    assertEquals(navController.currentDestination?.id, R.id.loginFragment)
}
```

With these modifications the test should pass. I added a [branch][1] to the repository with this solution.

If I'm not satisfied with this solution it's because I had an issue with one device: `java.lang.IllegalArgumentException: ID does not reference a View inside this Activity`. I don't know why but it seems with some devices there is a problem retrieving the `navController` using the id of the view.

  [1]: https://developer.android.com/guide/navigation/navigation-testing
  [2]: https://developer.android.com/guide/navigation/navigation-conditional
  [3]: https://github.com/laurentP22/hilt-conditional-navigation/tree/master
  [4]: https://developer.android.com/training/dependency-injection/hilt-testing#launchfragment
  [5]: https://developer.android.com/guide/fragments/test#create
  [6]: https://github.com/laurentP22/hilt-conditional-navigation/tree/fix/test-navigation
