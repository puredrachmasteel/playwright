package playwrightTraditional;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightStoreTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setUpAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeEach
    void setUp() {
        // Create context with video recording and clear cache/cookies
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));

        context.clearCookies();
        context.clearPermissions();
        page = context.newPage();
    }

    @AfterEach
    void tearDown() {
        // Close context to finalize video file
        context.close();
    }

    @AfterAll
    static void tearDownAll() {
        browser.close();
        playwright.close();
    }

    @Test
    void testBookstorePurchaseFlow() {
        page.setDefaultNavigationTimeout(90000);
        page.setDefaultTimeout(90000);
        page.navigate("https://depaul.bncollege.com/");
        // BookstoreTest
        page.getByPlaceholder("Enter your search details (").click();
        page.getByPlaceholder("Enter your search details (").fill("earbuds");
        page.getByPlaceholder("Enter your search details (").press("Enter");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.locator("#facet-brand").getByRole(AriaRole.LIST).getByText("brand JBL (12)").click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.getByText("Color Black (9)").click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.getByText("Price Over $50 (8)").click();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless"))).isVisible();
        assertThat(page.getByText("668972707").nth(1)).isVisible();
        assertThat(page.getByText("$164.98")).isVisible();
        assertThat(page.getByText("Adaptive noise cancelling")).isVisible();
        page.getByLabel("Add to cart").click();
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items"))).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items")).click();
        // ShoppingCartTest
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your Shopping Cart(1 Item)")).first()).isVisible();
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless"))).isVisible();
        assertThat(page.getByLabel("Quantity, edit and press")).hasValue("1");
        assertThat(page.getByText("$").first()).isVisible();
        page.getByText("FAST In-Store PickupDePaul").click();
        assertThat(page.getByText("$").nth(1)).isVisible();
        assertThat(page.getByText("$3.00", new Page.GetByTextOptions().setExact(true))).isVisible();
        assertThat(page.getByText("TBD")).isVisible();
        assertThat(page.getByText("$167.98")).isVisible();
        page.getByLabel("Enter Promo Code").click();
        page.getByLabel("Enter Promo Code").fill("TEST");
        page.getByLabel("Apply Promo Code").click();
        assertThat(page.locator("#js-voucher-result")).containsText("The coupon code entered is not valid.");
        page.getByLabel("Proceed To Checkout").first().click();
        // CreateAccountTest
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Create Account"))).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();
        // ContactInformationTest
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Contact Information"))).isVisible();
        page.getByPlaceholder("Please enter your first name").click();
        page.getByPlaceholder("Please enter your first name").fill("Jon");
        page.getByPlaceholder("Please enter your last name").click();
        page.getByPlaceholder("Please enter your last name").fill("Arbuckle");
        page.getByPlaceholder("Please enter a valid email").click();
        page.getByPlaceholder("Please enter a valid email").fill("ilovemondays@gmail.com");
        page.getByPlaceholder("Please enter a valid phone").click();
        page.getByPlaceholder("Please enter a valid phone").fill("3128888888");
        assertThat(page.getByText("$164.98").nth(2)).isVisible();
        assertThat(page.getByText("$3.00").nth(3)).isVisible();
        assertThat(page.getByText("TBD").nth(2)).isVisible();
        assertThat(page.getByText("$167.98").nth(1)).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        // PickupTest
        assertThat(page.getByText("Jon Arbuckle")).isVisible();
        assertThat(page.getByText("ilovemondays@gmail.com")).isVisible();
        assertThat(page.getByText("13128888888")).isVisible();
        assertThat(page.locator("#bnedPickupPersonForm").getByText("DePaul University Loop Campus")).isVisible();
        assertThat(page.getByText("I'll pick them up")).isVisible();
        assertThat(page.getByText("$164.98").nth(2)).isVisible();
        assertThat(page.getByText("$3.00").nth(3)).isVisible();
        assertThat(page.getByText("TBD").nth(2)).isVisible();
        assertThat(page.getByText("$167.98").nth(1)).isVisible();
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).nth(1)).isVisible();
        assertThat(page.getByText("$164.98").nth(3)).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
        // PaymentTest
        assertThat(page.getByText("$164.98").nth(2)).isVisible();
        assertThat(page.getByText("$3.00").nth(3)).isVisible();
        assertThat(page.getByText("$17.").nth(1)).isVisible();
        assertThat(page.getByText("$185.20").nth(1)).isVisible();
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JBL Quantum True Wireless")).nth(1)).isVisible();
        assertThat(page.getByText("$164.98").nth(3)).isVisible();
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();
        // EmptyCartTest
        page.getByLabel("Remove product JBL Quantum").click();
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your cart is empty"))).isVisible();
        }
    }
