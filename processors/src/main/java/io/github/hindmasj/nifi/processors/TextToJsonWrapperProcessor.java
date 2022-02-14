package io.github.hindmasj.nifi.processors;

import org.apache.commons.io.IOUtils;

import org.apache.nifi.annotation.behavior.*;
import org.apache.nifi.annotation.behavior.InputRequirement.Requirement;
import org.apache.nifi.annotation.documentation.*;
import org.apache.nifi.annotation.lifecycle.OnScheduled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.*;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.processor.util.StandardValidators;

import com.google.gson.*;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Tags({"Text","JSON"})
@CapabilityDescription("Wraps any plain text messages as JSON")
@InputRequirement(Requirement.INPUT_REQUIRED)
public class TextToJsonWrapperProcessor extends AbstractProcessor {

  public static final Relationship SUCCESS = new Relationship.Builder()
  .name("Success")
  .description("The processing has completed successfully.")
  .build();

  public static final Relationship FAILURE = new Relationship.Builder()
  .name("Failure")
  .description("There has been an issue processing the messages.")
  .build();

  private Set<Relationship> relationships =
  new HashSet<Relationship>();

  /** Allows NiFi to get the current relationships. */
  @Override
  public Set<Relationship> getRelationships() {
    return this.relationships;
  }

  /** Allows NiFi to set the the relationships. */
  public void setRelationships(Set<Relationship> relationships) {
    this.relationships = relationships;
  }

  /** Initialises the relationships. */
  @Override
  protected void init(final ProcessorInitializationContext context) {
    this.relationships.add(SUCCESS);
    this.relationships.add(FAILURE);
  }

  /** Convert JSONArray back to ByteArray for storage in flow file. */
  private static byte[] objectToByteArray(Object object) {
      return ((object.toString()).getBytes());
  }

  /** Do the actual wrapping */
  private JsonArray wrapStringAsJson(String flowString){
    JsonArray responseArray=new JsonArray();
    if(!flowString.isEmpty()){
      JsonObject wrapper=new JsonObject();
      wrapper.addProperty("wraps",flowString);
      responseArray.add(wrapper);
    }
    return responseArray;
  }

  /** The entry point for processing. */
  @Override
  public void onTrigger(final ProcessContext context, final ProcessSession session) throws ProcessException {
    FlowFile flowFile = session.get();
    if (flowFile == null) {return;}
    final AtomicReference<JsonArray> response = new AtomicReference<JsonArray>();

    //try{
      session.read(flowFile, in-> {
        String flowDataString=IOUtils.toString(in,StandardCharsets.UTF_8);
        try{
          JsonArray flowMessageArray=JsonParser.parseString(flowDataString).getAsJsonArray();
          flowMessageArray.forEach(message->{
            JsonObject o=message.getAsJsonObject();
          });
          response.set(flowMessageArray);
        }catch(IllegalStateException e){
          // String is not a JSON object. Wrap it in JSON
          response.set(wrapStringAsJson(flowDataString));
        }
      });

      session.transfer(session.write(flowFile, out -> {
        out.write(objectToByteArray(response.toString()));
      }),SUCCESS);
    //}
  }

}
