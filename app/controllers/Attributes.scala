package controllers

import com.google.api.client.auth.oauth2.Credential
import play.api.libs.typedmap.TypedKey

object Attributes {
  val credential: TypedKey[Credential] = TypedKey.apply[Credential]("credential")
}
