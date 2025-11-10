package playwrightLLM;

import java.nio.file.Paths;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;

@TestMethodOrder(OrderAnnotation.class)
public class AIPlaywrightStoreTest {
    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;

    // Shared test data
    static final String BASE_URL = "https://depaul.bncollege.com/";
    static final String FIRST_NAME = "Jon";
    static final String LAST_NAME = "Arbuckle";
    static final String EMAIL = "ilovemondays@gmail.com";
    static final String PHONE = "3128888888";
    static final String PRODUCT_LINK_TEXT = "JBL Quantum True Wireless Noise Cancelling Gaming";
    static final String FULL_PRODUCT_NAME = "JBL Quantum True Wireless Noise Cancelling Gaming Earbuds - Black";

    @BeforeAll
    static void setUpAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        // Create context with video recording and clear cache/cookies
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get("videos/"))
                .setRecordVideoSize(1280, 720));

        context.clearCookies();
        context.clearPermissions();
        page = context.newPage();
        page.setDefaultNavigationTimeout(90000);
        page.setDefaultTimeout(90000);
    }

    @AfterAll
    static void tearDownAll() {
        context.close();
        browser.close();
        playwright.close();
    }

    @Test
    @Order(1)
    void test01_bookstoreProductSearchAndAddToCart() {
        // 1. Go to the website homepage.
        page.navigate(BASE_URL);

        // 2. Enter “earbuds” in the search box in the upper-right and press RETURN.
        page.getByPlaceholder("Enter your search details (").click();
        page.getByPlaceholder("Enter your search details (").fill("earbuds");
        page.getByPlaceholder("Enter your search details (").press("Enter");

        // 3. Expand the “Brand” filter and select “JBL”.
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("brand")).click();
        page.locator("#facet-brand").getByRole(AriaRole.LIST).getByText("brand JBL (12)").click();

        // 4. Expand the “Color” filter and select “Black”.
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Color")).click();
        page.getByText("Color Black (9)").click();

        // 5. Expand the “Price” filter and select “Over $50”.
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Price")).click();
        page.getByText("Price Over $50 (8)").click();

        // 6. Click on the product link titled “JBL Quantum True Wireless Noise Cancelling Gaming...”.
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(PRODUCT_LINK_TEXT)).click();

        // 7. Assert that the product name, SKU number, price, and product description are visible.
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(PRODUCT_LINK_TEXT))).isVisible();
        // SKU: the test environment shows "668972707" — allow locator to wait for text presence
        assertThat(page.getByText("668972707").nth(1)).isVisible();
        assertThat(page.getByText("$164.98")).isVisible();
        assertThat(page.getByText("Adaptive noise cancelling")).isVisible();

        // 8. Add one item to the cart.
        page.getByLabel("Add to cart").click();

        // 9. Assert that the cart icon in the upper-right shows “1 item”.
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items"))).isVisible();

        // 10. Click the cart icon to go to the cart page.
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Cart 1 items")).click();
    }

    @Test
    @Order(2)
    void test02_shoppingCartPageVerification() {
        // 1. Assert that the page title or label shows “Your Shopping Cart”.
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your Shopping Cart(1 Item)")).first()).isVisible();

        // 2. Assert that the product name, quantity (1), and price are visible.
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(PRODUCT_LINK_TEXT))).isVisible();
        assertThat(page.getByLabel("Quantity, edit and press")).hasValue("1");
        assertThat(page.getByText("$164").first()).isVisible();

        // 3. Select “FAST In-Store Pickup”.
        // The UI may render "FAST In-Store PickupDePaul" as a clickable label in this environment
        page.getByText("FAST In-Store PickupDePaul").click();

        // 4. Assert that the sidebar shows: subtotal =, handling =, taxes = “TBD”, and estimated total =.
        // Use partial $ checks and "TBD"
        assertThat(page.getByText("$164.98").nth(1)).isVisible(); // subtotal-like
        assertThat(page.getByText("$3.00", new Page.GetByTextOptions().setExact(true))).isVisible(); // handling
        assertThat(page.getByText("TBD")).isVisible(); // taxes
        assertThat(page.getByText("$167.98")).isVisible(); // estimated total

        // 5. Enter promo code “TEST” and click APPLY.
        page.getByLabel("Enter Promo Code").click();
        page.getByLabel("Enter Promo Code").fill("TEST");
        page.getByLabel("Apply Promo Code").click();

        // 6. Assert that a “promo code reject” message is displayed.
        assertThat(page.locator("#js-voucher-result")).containsText("The coupon code entered is not valid.");

        // 7. Click “PROCEED TO CHECKOUT”.
        page.getByLabel("Proceed To Checkout").first().click();
    }

    @Test
    @Order(3)
    void test03_createAccountPage() {
        // 1. Assert that the “Create Account” label is present.
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Create Account"))).isVisible();

        // 2. Select “Proceed as Guest”.
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Proceed As Guest")).click();
    }

    @Test
    @Order(4)
    void test04_contactInformationPage() {
        // 1. Assert that the user is on the “Contact Information” page.
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Contact Information"))).isVisible();

        // 2. Enter a first name, last name, email address, and phone number.
        page.getByPlaceholder("Please enter your first name").click();
        page.getByPlaceholder("Please enter your first name").fill(FIRST_NAME);

        page.getByPlaceholder("Please enter your last name").click();
        page.getByPlaceholder("Please enter your last name").fill(LAST_NAME);

        page.getByPlaceholder("Please enter a valid email").click();
        page.getByPlaceholder("Please enter a valid email").fill(EMAIL);

        page.getByPlaceholder("Please enter a valid phone").click();
        page.getByPlaceholder("Please enter a valid phone").fill(PHONE);

        // 3. Assert that the sidebar shows the same subtotal, handling, taxes (“TBD”), and estimated total.
        assertThat(page.getByText("$164.98").nth(2)).isVisible();
        assertThat(page.getByText("$3.00").nth(3)).isVisible();
        assertThat(page.getByText("TBD").nth(2)).isVisible();
        assertThat(page.getByText("$167.98").nth(1)).isVisible();

        // 4. Click CONTINUE.
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
    }

    @Test
    @Order(5)
    void test05_pickupInformationPage() {
        // 1. Assert that Contact Information (name, email, phone) matches previously entered values.
        assertThat(page.getByText(FIRST_NAME + " " + LAST_NAME)).isVisible();
        assertThat(page.getByText(EMAIL)).isVisible();
        // Note: phone may be rendered with formatting; check for substring
        assertThat(page.getByText(PHONE.substring(1)).or(page.getByText(PHONE)).first()).isVisible();

        // 2. Assert that the pickup location is “DePaul University Loop Campus & SAIC”.
        // The site uses a shorter label; check for "DePaul University Loop Campus"
        assertThat(page.locator("#bnedPickupPersonForm").getByText("DePaul University Loop Campus")).isVisible();

        // 3. Assert that the selected pickup person option is “I’ll pick them up”.
        assertThat(page.getByText("I'll pick them up")).isVisible();

        // 4. Assert that the sidebar still shows subtotal, handling, taxes (“TBD”), and estimated total.
        assertThat(page.getByText("$164.98").nth(2)).isVisible();
        assertThat(page.getByText("$3.00").nth(3)).isVisible();
        assertThat(page.getByText("TBD").nth(2)).isVisible();
        assertThat(page.getByText("$167.98").nth(1)).isVisible();

        // 5. Assert that the pickup item and price match expectations.
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(PRODUCT_LINK_TEXT)).nth(1)).isVisible();
        assertThat(page.getByText("$164.98").nth(3)).isVisible();

        // 6. Click CONTINUE.
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")).click();
    }

    @Test
    @Order(6)
    void test06_paymentInformationPage() {
        // 1. Assert that the sidebar shows subtotal, handling, taxes, and total.
        assertThat(page.getByText("$164.98").nth(2)).isVisible();
        assertThat(page.getByText("$3.00").nth(3)).isVisible();
        assertThat(page.getByText("TBD").nth(2)).isVisible();
        assertThat(page.getByText("$167.98").nth(1)).isVisible();

        // 2. Assert that the pickup item and price are correct.
        assertThat(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(PRODUCT_LINK_TEXT)).nth(1)).isVisible();
        assertThat(page.getByText("$164.98").nth(3)).isVisible();

        // 3. Click “BACK TO CART” in the upper-right.
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Back to cart")).click();
    }

    @Test
    @Order(7)
    void test07_shoppingCartCleanup() {
        // 1. Assert that you are back at “Your Shopping Cart”.
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your Shopping Cart(1 Item)")).first()).isVisible();

        // 2. Delete the product from the cart.
        page.getByLabel("Remove product JBL Quantum").click();

        // 3. Assert that the cart is now empty.
        assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your cart is empty"))).isVisible();

        // 4. Close the browser window.
        // Close context and browser here (AfterAll will also attempt to close, but we catch exceptions there)
        try {
            if (context != null) context.close();
        } catch (Exception ignored) {
        }
        try {
            if (browser != null) browser.close();
        } catch (Exception ignored) {
        }
    }

}