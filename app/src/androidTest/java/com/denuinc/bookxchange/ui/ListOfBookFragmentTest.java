package com.denuinc.bookxchange.ui;


import android.arch.lifecycle.MutableLiveData;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.denuinc.bookxchange.R;
import com.denuinc.bookxchange.SingleFragmentActivity;
import com.denuinc.bookxchange.binding.FragmentBindingAdapters;
import com.denuinc.bookxchange.utils.EspressoTestUtils;
import com.denuinc.bookxchange.utils.TaskExecutorWithIdlingResourceRule;
import com.denuinc.bookxchange.utils.TestUtils;
import com.denuinc.bookxchange.utils.ViewModelUtil;
import com.denuinc.bookxchange.vo.Book;
import com.denuinc.bookxchange.vo.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ListOfBookFragmentTest {

    @Rule
    public ActivityTestRule<SingleFragmentActivity> activityTestRule = new ActivityTestRule<>(SingleFragmentActivity.class, true, true);
    @Rule
    public TaskExecutorWithIdlingResourceRule executorRule =
            new TaskExecutorWithIdlingResourceRule();

    private FragmentBindingAdapters fragmentBindingAdapters;

    private BooksViewModel viewModel;

    private MutableLiveData<Resource<List<Book>>> results = new MutableLiveData<>();
    private MutableLiveData<BooksViewModel.LoadMoreState> loadMoreStatus = new MutableLiveData<>();

    @Before
    public void init() {
        EspressoTestUtils.disableProgressBarAnimations(activityTestRule);
        BookListFragment bookListFragment = new BookListFragment();
        viewModel = mock(BooksViewModel.class);
        doReturn(loadMoreStatus).when(viewModel).getLoadMoreStatus();
        when(viewModel.getResults()).thenReturn(results);

        fragmentBindingAdapters = mock(FragmentBindingAdapters.class);
        bookListFragment.viewModelFactory = ViewModelUtil.createFor(viewModel);
        bookListFragment.dataBindingComponent = () -> fragmentBindingAdapters;
        activityTestRule.getActivity().setFragment(bookListFragment);
    }

    @Test
    public void search() {
        onView(withId(R.id.avi)).check(matches(isDisplayed()));
        verify(viewModel).setQuery("subject:novel");
        results.postValue(Resource.loading(null));
        onView(withId(R.id.avi)).check(matches(isDisplayed()));
    }

    @Test
    public void loadResults() {
        Book book = TestUtils.createBook("book123");
        results.postValue(Resource.success(Collections.singletonList(book)));
        onView(withId(R.id.avi)).check(matches(not(isDisplayed())));
    }

    @Test
    public void emptyResult() {
        results.postValue(Resource.success(new ArrayList<>()));
        onView(withId(R.id.image_view_empty)).check(matches(isDisplayed()));
    }
}
