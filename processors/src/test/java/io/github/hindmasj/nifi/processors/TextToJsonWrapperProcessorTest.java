package io.github.hindmasj.nifi.processors;

import java.util.List;

import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class TextToJsonWrapperProcessorTest {

  private TestRunner testRunner;

  @Before
  public void init() {
      testRunner = TestRunners.newTestRunner(TextToJsonWrapperProcessor.class);
  }

  @Test
  public void testEmptyString() {
    MockFlowFile flowFile=getSuccessResponse("");
    flowFile.assertContentEquals("[]");
  }

  @Test
  public void testSimpleString() {
    MockFlowFile flowFile=getSuccessResponse("hello");
    flowFile.assertContentEquals("[{\"wraps\":\"hello\"}]");
  }

  @Test
  public void testSomeJson() {
    String input="[{\"name\":\"hello\"},{\"value\":\"world\"}]";
    MockFlowFile flowFile=getSuccessResponse(input);
    flowFile.assertContentEquals(input);
  }

  private MockFlowFile getSuccessResponse(String input){
    testRunner.enqueue(input);
    testRunner.run();
    testRunner.assertAllFlowFilesTransferred("Success");

    List<MockFlowFile> output=
      testRunner.getFlowFilesForRelationship("Success");
    assertNotNull(output);
    assertEquals(output.size(),1);
    return output.get(0);
  }

}
