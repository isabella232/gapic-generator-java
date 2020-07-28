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

package com.google.api.generator.gapic.composer;

import static junit.framework.Assert.assertEquals;

import com.google.api.generator.engine.writer.JavaWriterVisitor;
import com.google.api.generator.gapic.model.GapicClass;
import com.google.api.generator.gapic.model.Message;
import com.google.api.generator.gapic.model.Service;
import com.google.api.generator.gapic.protoparser.Parser;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.Descriptors.ServiceDescriptor;
import com.google.showcase.v1beta1.EchoOuterClass;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;

public class DemoClassComposerTest {
  private ServiceDescriptor echoService;
  private FileDescriptor echoFileDescriptor;

  @Before
  public void setUp() {
    echoFileDescriptor = EchoOuterClass.getDescriptor();
    echoService = echoFileDescriptor.getServices().get(0);
    assertEquals(echoService.getName(), "Echo");
  }

  @Test
  public void generateServiceClasses() {
    Map<String, Message> messageTypes = Parser.parseMessages(echoFileDescriptor);
    List<Service> services = Parser.parseService(echoFileDescriptor, messageTypes);
    Service echoProtoService = services.get(0);
    GapicClass clazz = DemoClassComposer.instance().generate(echoProtoService, messageTypes);

    JavaWriterVisitor visitor = new JavaWriterVisitor();
    clazz.classDefinition().accept(visitor);
    assertEquals(EXPECTED_CLASS_STRING, visitor.write());
  }

  private static final String EXPECTED_CLASS_STRING =
      "package com.google.showcase.v1beta1.demo;\n"
          + "\n"
          + "import com.google.api.gax.core.BackgroundResource;\n"
          + "import com.google.longrunning.stub.GrpcOperationsStub;\n"
          + "import java.util.concurrent.ThreadLocalRandom;\n"
          + "import javax.annotation.Generated;\n"
          + "\n"
          + "@Generated(\"by gapic-generator-java\")\n"
          + "public class EchoDemo {\n"
          + "  private final BackgroundResource backgroundResources;\n"
          + "  private final GrpcOperationsStub operationsStub;\n"
          + "\n"
          + "  public int foobar(int max) {\n"
          + "    int x = ThreadLocalRandom.current().nextInt(0, max);\n"
          + "    return x;\n"
          + "  }\n"
          + "}\n";
}