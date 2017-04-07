/*
 * Copyright © 2015 - 2017 Lightbend, Inc. <http://www.lightbend.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lightbend.paradox.markdown

import com.lightbend.paradox.tree.Tree.Location

class VarDirectiveSpec extends MarkdownBaseSpec {

  val testProperties = Map("version" -> "1.2.3")

  implicit val context: Location[Page] => Writer.Context = { loc =>
    writerContext(loc).copy(properties = testProperties)
  }

  "Var substitution" should "insert property values" in {
    markdown("$version$") shouldEqual html("<p>1.2.3</p>")
  }

  it should "work within markdown inlines" in {
    markdown("Version is *$version$*.") shouldEqual html("<p>Version is <em>1.2.3</em>.</p>")
  }

  it should "work within markdown blocks" in {
    markdown("- $version$") shouldEqual html("<ul><li>1.2.3</li></ul>")
  }

  it should "work within link labels" in {
    markdown("[Version $version$](version.html)") shouldEqual html("""<p><a href="version.html">Version 1.2.3</a></p>""")
  }

  it should "work within other inline directives" in {
    markdown("@ref:[Version $version$](test.md)") shouldEqual html("""<p><a href="test.html">Version 1.2.3</a></p>""")
  }

  it should "retain whitespace before and after" in {
    markdown("The $version$ version.") shouldEqual html("<p>The 1.2.3 version.</p>")
  }

  it should "support escaping the $ delimiter" in {
    markdown("The \\$version$ version.") shouldEqual html("<p>The $version$ version.</p>")
    markdown("The \\$version\\$ ver.$s$.ion.") shouldEqual html("<p>The $version$ ver.&lt;s&gt;.ion.</p>")
    markdown("The $ver\\$ion$ version.") shouldEqual html("<p>The &lt;ver$ion&gt; version.</p>")
  }

  it should "support the legacy @var directive notation" in {
    markdown("@var[version]") shouldEqual html("<p>1.2.3</p>")
  }

  it should "support the legacy 'var:' alternative name" in {
    markdown("@var:[version]") shouldEqual html("<p>1.2.3</p>")
  }

  it should "parse but ignore legacy directive source and attributes" in {
    markdown("The @var[version] (xxx) { .var a=1 } version.") shouldEqual html("<p>The 1.2.3 version.</p>")
  }
}
