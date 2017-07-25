package controllers

import com.google.api.client.auth.oauth2.Credential
import play.api.mvc.{Request, WrappedRequest}

class CredentialRequest[A](val credential: Credential, request: Request[A]) extends WrappedRequest[A](request)
