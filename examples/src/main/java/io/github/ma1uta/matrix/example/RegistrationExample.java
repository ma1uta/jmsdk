/*
 * Copyright Anatoliy Sablin tolya@sablin.xyz
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

package io.github.ma1uta.matrix.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ma1uta.matrix.client.StandaloneClient;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.common.ExceptionResponse;
import io.github.ma1uta.matrix.common.UserInteractiveResponse;
import io.github.ma1uta.matrix.impl.exception.MatrixException;

public class RegistrationExample {

    public static void main(String[] args) {
        String domain = "ru-matrix.org";
        String localpart = "new_account";
        char[] password = "my_very_secret_password".toCharArray();

        StandaloneClient mxClient = new StandaloneClient.Builder().domain(domain).build();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(localpart);
        registerRequest.setPassword(password);

        syncRegistration(mxClient, registerRequest);
        asyncRegistration(mxClient, registerRequest);
    }

    private static void syncRegistration(StandaloneClient mxClient, RegisterRequest registerRequest) {
        try {
            processLogin(mxClient.account().register(registerRequest));
        } catch (Exception e) {
            processException(e);
        }
    }

    private static void asyncRegistration(StandaloneClient mxClient, RegisterRequest registerRequest) {
        mxClient.accountAsync().register(registerRequest).whenComplete((loginResponse, throwable) -> {
            if (throwable != null) {
                processException(throwable);
            }
            if (loginResponse != null) {
                processLogin(loginResponse);
            }
        });
    }

    private static void processLogin(LoginResponse loginResponse) {
        System.out.println("LOGIN!!!");
        System.out.println(loginResponse.getUserId());
        System.out.println(loginResponse.getDeviceId());
        System.out.println(loginResponse.getAccessToken());
    }

    private static void processException(Throwable e) {
        Throwable cause = e;
        while (!(cause instanceof MatrixException) && cause.getCause() != null) {
            cause = cause.getCause();
        }
        if (cause instanceof MatrixException) {
            MatrixException matrixException = (MatrixException) cause;
            ExceptionResponse response = matrixException.getResponse();
            if (response instanceof UserInteractiveResponse) {
                UserInteractiveResponse userInteractiveResponse = (UserInteractiveResponse) response;
                System.out.println(userInteractiveResponse.getSession());
                ObjectMapper mapper = new ObjectMapper();
                try {
                    System.out.println(mapper.writeValueAsString(userInteractiveResponse));
                } catch (JsonProcessingException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        }
    }
}
