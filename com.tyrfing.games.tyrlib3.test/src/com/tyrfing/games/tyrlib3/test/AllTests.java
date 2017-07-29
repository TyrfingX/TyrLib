package com.tyrfing.games.tyrlib3.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.tyrfing.games.tyrlib3.test.network.NetworkTest;

@RunWith(Suite.class)
@SuiteClasses({
	NetworkTest.class
})
public class AllTests {

}
