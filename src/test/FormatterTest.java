/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import handlers.BirdStatFormatter;

/**
 * @author Rever
 *
 */
public class FormatterTest {
	BirdStatFormatter bsfGood;
	BirdStatFormatter bsfBad;
	
	static File testBad;
	static File testGood;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testBad = new File("./resources/test/testBad.txt");
		if(!testBad.exists()) {
			testBad.createNewFile();
			FileWriter bsfbWriter = new FileWriter(testBad);
			BufferedWriter bw = new BufferedWriter(bsfbWriter);
			bw.append("test 1 2 3");
			bw.newLine();
			bw.append("the end");
			bw.close();
		}
		
		testGood = new File("./resources/test/testGood.txt");
		if(!testGood.exists()) {
			fail("resources in the wrong spot");
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		bsfBad = new BirdStatFormatter(testBad, "testBad.csv");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void createTest() throws IOException {
		FileReader bsfbReader = new FileReader(testBad);
		BufferedReader bfBad = new BufferedReader(bsfbReader);
	
		FileReader bsfgReader = new FileReader(testGood);
		BufferedReader bfGood = new BufferedReader(bsfgReader);
	
		
	}

}
