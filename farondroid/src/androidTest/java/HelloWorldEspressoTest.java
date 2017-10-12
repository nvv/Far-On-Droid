import android.graphics.drawable.ColorDrawable;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.openfarmanager.android.App;
import com.openfarmanager.android.Main;
import com.openfarmanager.android.R;

import org.hamcrest.Description;
import org.hamcrest.core.AllOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.CoreMatchers.allOf;

/**
 * @author Vlad Namashko
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HelloWorldEspressoTest {

    private static final String TAG = "Test";


    @Rule
    public ActivityTestRule<Main> mActivityRule =
            new ActivityTestRule(Main.class);

//    @Test
//    public void listGoesOverTheFold() {
////        onView(withText("Hello world!")).check(matches(isDisplayed()));
//
//
////        Espresso.registerIdlingResources()
//
////        onView(withText("More")).perform(ViewActions.click());
////
////        onView(withText("Actions")).check(matches(isDisplayed()));
//
//    }
//
//    @Test
//    public void testList() {
////        RecyclerView recyclerView = (RecyclerView) mActivityRule.getActivity().findViewById(android.R.id.list);
////        RecyclerView.Adapter adapter = recyclerView.getAdapter();
//
////        onView(withId(android.R.id.list))
////                .perform(RecyclerViewActions.actionOnItem(
////                        hasDescendant(withText("Folder")),
////                        click()));
//
//
//    }


    @Test
    public void testFocus() {
        onView(withId(R.id.panel_left)).perform(ViewActions.swipeDown());

//        allOf(withId(R.id.current_path), withParent(withId(R.id.panel_left)))

        onView(AllOf.allOf(withId(R.id.current_path), withParent(withId(R.id.action_bar_left)))).check(matches(new BoundedMatcher<View, View>(View.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("color: ");
            }

            @Override
            protected boolean matchesSafely(View view) {
                return ((ColorDrawable) view.getBackground()).getColor() == App.sInstance.getSettings().getSecondaryColor();
            }
        }));
    }


//
//    private final IdlingResource mWaitForDialogIdling = new IdlingResource() {
//
//        ResourceCallback callback;
//
//        @Override
//        public String getName() {
//            return "wait for action dialog";
//        }
//
//        @Override
//        public boolean isIdleNow() {
//
//            doesNotExist()
//
//            if (onView(withText("Actions")).) {
//
//                if (callback != null) {
//                    callback.onTransitionToIdle();
//                }
//                return true;
//            }
//            return false;
//        }
//
//        @Override
//        public void registerIdleTransitionCallback(ResourceCallback callback) {
//            this.callback = callback;
//        }
//    };

}

