/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qunar.im.auth;

/**
 * Indicates that {@link OtpSource} failed to performed the requested operation.
 *
 * @author klyubin@google.com (Alex Klyubin)
 */
public class OtpSourceException extends Exception {
  public OtpSourceException(String message) {
    super(message);
  }

  public OtpSourceException(String message, Throwable cause) {
    super(message, cause);
  }
}
