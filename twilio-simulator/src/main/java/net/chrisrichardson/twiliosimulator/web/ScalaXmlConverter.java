package net.chrisrichardson.twiliosimulator.web;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import scala.xml.NodeSeq;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;

public class ScalaXmlConverter implements HttpMessageConverter<NodeSeq> {

  @Override
  public boolean canRead(Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return NodeSeq.class.isAssignableFrom(clazz);
  }

  @Override
  public List<MediaType> getSupportedMediaTypes() {
    return Collections.singletonList(MediaType.APPLICATION_XML);
  }

  @Override
  public NodeSeq read(Class<? extends NodeSeq> clazz, HttpInputMessage inputMessage) throws IOException,
      HttpMessageNotReadableException {
    throw new UnsupportedOperationException("implement me");
  }

  @Override
  public void write(NodeSeq t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException,
      HttpMessageNotWritableException {
    outputMessage.getHeaders().setContentType(MediaType.APPLICATION_XML);
    OutputStreamWriter osw = new OutputStreamWriter(outputMessage.getBody());
    osw.write(t.toString());
    osw.flush();
  }
}
