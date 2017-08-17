package services

import com.google.api.client.auth.oauth2.Credential
import model.resource.Usage

trait ResourceService {
  def fetchUsage(credential: Credential): Usage
}
