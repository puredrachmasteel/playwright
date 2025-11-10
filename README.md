https://github.com/puredrachmasteel/playwright

## Manual Testing
The manual testing proved to be much easier to write and control while looking for errors that might cause the test to fail,
and I had the luxury of going through the errors myself and figuring out what went wrong, if it was directly my own fault or 
if it was timeout. Due to this, my generated tests felt more reliable and accurate than the AI-assisted ones, along with my
ability to directly maintain the structure of the tests to ensure that they passed more and more often. The only limitations 
I encountered for Manual Testing was ensuring that my tests captured what appeared on the screen during the test, and my 
choice to include everything within one test, which ultimately made it harder to track errors when they occurred while 
checking why tests would fail during the development phase.

## AI-Assisted Testing
AI-assisted testing felt a lot harder to generate, but errors were easier to look for because the AI did provide 7
different tests to keep track of rather than piling them all into one test like I did manually. Running the tests was a
bit difficult, as it previously did make a video before each test and reset the website before each test, which would 
cause only the first test to pass because the window would close and lose its place in the process of buying the headphones.
Ultimately, the tests here felt a lot more flaky, since sometimes they all pass and while other times one or two of them 
may completely fail due to a timeout error. Actually getting the tests to all pass was the main issue that I occurred due
to this flakiness, and the limitations of me not being clear enough to the AI so it wouldn't set up a @BeforeEach and an
@AfterEach that would completely break the flow of the seven tests it made, that I have work in only one test.