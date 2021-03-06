// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.api.generator.gapic.protoparser;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

import com.google.api.generator.gapic.model.Message;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.MethodDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import com.google.showcase.v1beta1.TestingOuterClass;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;

public class HttpRuleParserTest {
  @Test
  public void parseHttpAnnotation_basic() {
    FileDescriptor testingFileDescriptor = TestingOuterClass.getDescriptor();
    ServiceDescriptor testingService = testingFileDescriptor.getServices().get(0);
    assertEquals(testingService.getName(), "Testing");

    Map<String, Message> messages = Parser.parseMessages(testingFileDescriptor);

    // CreateSession method.
    MethodDescriptor rpcMethod = testingService.getMethods().get(0);
    Message inputMessage = messages.get("CreateSessionRequest");
    Optional<List<String>> httpBindingsOpt =
        HttpRuleParser.parseHttpBindings(rpcMethod, inputMessage, messages);
    assertFalse(httpBindingsOpt.isPresent());

    // VerityTest method.
    rpcMethod = testingService.getMethods().get(testingService.getMethods().size() - 1);
    inputMessage = messages.get("VerifyTestRequest");
    httpBindingsOpt = HttpRuleParser.parseHttpBindings(rpcMethod, inputMessage, messages);
    assertTrue(httpBindingsOpt.isPresent());
    assertThat(httpBindingsOpt.get()).containsExactly("name");
  }

  @Test
  public void parseHttpAnnotation_missingFieldFromMessage() {
    FileDescriptor testingFileDescriptor = TestingOuterClass.getDescriptor();
    ServiceDescriptor testingService = testingFileDescriptor.getServices().get(0);
    assertEquals(testingService.getName(), "Testing");

    Map<String, Message> messages = Parser.parseMessages(testingFileDescriptor);

    // VerityTest method.
    MethodDescriptor rpcMethod =
        testingService.getMethods().get(testingService.getMethods().size() - 1);
    Message inputMessage = messages.get("CreateSessionRequest");
    assertThrows(
        IllegalStateException.class,
        () -> HttpRuleParser.parseHttpBindings(rpcMethod, inputMessage, messages));
  }
}
