package com.softwaremill.codebrag.finders.user

import org.bson.types.ObjectId
import com.softwaremill.codebrag.domain.{UserBrowsingContext, UserSettings}
import com.softwaremill.codebrag.service.data.UserJson

case class LoggedInUserView(
  id: ObjectId,
  login: String,
  fullName: String,
  email: String,
  admin: Boolean,
  settings: UserSettings,
  browsingContext: UserBrowsingContext)

object LoggedInUserView {
   def apply(user: UserJson, userContext: UserBrowsingContext) = {
     new LoggedInUserView(user.idAsObjectId, user.login, user.fullName, user.email, user.admin, user.settings, userContext)
   }
 }
