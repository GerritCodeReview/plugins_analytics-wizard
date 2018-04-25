// Copyright (C) 2017 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.googlesource.gerrit.plugins.analytics.wizard

import java.io.{ByteArrayOutputStream, PrintWriter}
import java.util
import javax.servlet._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse, HttpServletResponseWrapper}

import com.google.gerrit.extensions.registration.DynamicItem
import com.google.gerrit.httpd.WebSession
import com.google.gerrit.server.AccessPath
import com.google.inject.{Inject, Singleton}
import org.slf4j.{Logger, LoggerFactory}

@Singleton
class XAuthFilter @Inject()(val webSession: DynamicItem[WebSession]) extends Filter {
  val log: Logger = LoggerFactory.getLogger(classOf[XAuthFilter])

  override def init(filterConfig: FilterConfig) {}

  override def destroy() {}

  override def doFilter(req: ServletRequest, resp: ServletResponse, chain: FilterChain) {
    val session = webSession.get
    val httpReq = req.asInstanceOf[HttpServletRequest]
    val httpResp = resp.asInstanceOf[HttpServletResponse];

    val gerritAuth = session.getXGerritAuth
    if (gerritAuth != null) {
      session.setAccessPathOk(AccessPath.REST_API, true)
      log.debug("Injecting X-Gerrit-Auth for {}", httpReq.getRequestURI)
      val httpRespWrapper: HttpServletResponse = new HttpServletResponseWrapper(httpResp) {
        private var origContentLength = 0

        override def setHeader(name: String, value: String) {
          if (name.equalsIgnoreCase("Content-Length")) {
            origContentLength = Integer.parseInt(value)
          } else {
            super.setHeader(name, value)
          }
        }

        override def getWriter(): PrintWriter = super.getWriter()

        override def getOutputStream(): ServletOutputStream = {
          new TokenReplaceOutputStream(
            getResponse().asInstanceOf[HttpServletResponse],
            origContentLength,
            "@X-Gerrit-Auth".getBytes(),
            gerritAuth.getBytes())
        }
      }

      httpRespWrapper.setHeader("Cache-Control", "private, no-cache, no-store, must-revalidate, max-age=0")
      httpRespWrapper.setHeader("Pragma", "no-cache")
      httpRespWrapper.setHeader("Expires", "0")
      chain.doFilter(req, httpRespWrapper)
    } else {
      val res = resp.asInstanceOf[HttpServletResponse]
      res.sendError(HttpServletResponse.SC_UNAUTHORIZED)
    }
  }
}

class TokenReplaceOutputStream(val resp: HttpServletResponse,
                               outLen: Int,
                               token: Array[Byte],
                               replace: Array[Byte]) extends ServletOutputStream {

  private val outBuff = new ByteArrayOutputStream(outLen)

  override def write(b: Int) {
    outBuff.write(b)
  }

  override def flush() {
    if (outBuff.size() >= outLen) {

      val outData = outBuff.toByteArray()
      val cmp = new Array[Byte](token.length)
      val convertedData = new ByteArrayOutputStream(outData.length)

      var i = 0
      while (i < outData.length) {
        val b = outData(i);
        if (b != token(0) || (outData.length - i) < token.length) {
          convertedData.write(outData, i, 1)
        } else {

          System.arraycopy(outData, i, cmp, 0, token.length)
          if (util.Arrays.equals(cmp, token)) {
            convertedData.write(replace);
            i += token.length - 1;
          }
        }
        i += 1
      }

      resp.setHeader("Content-Length", "" + convertedData.size())

      val out = resp.getOutputStream()
      out.write(convertedData.toByteArray())
      out.flush()
    }
  }

  override def close() {
    flush()
  }

  override def write(b: Array[Byte]) {
    outBuff.write(b)
    flush()
  }

  override def write(b: Array[Byte], off: Int, len: Int) {
    outBuff.write(b, off, len)
    flush()
  }

  override def setWriteListener(writeListener: WriteListener) {}

  override def isReady(): Boolean = true
}