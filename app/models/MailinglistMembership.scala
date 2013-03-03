package models


/**
 * TODO: implement relation between user and mailinglist
 */
case class MailinglistMembership(
    user: String,
    mailinglist: String,
    isApproved: Boolean,
    isActive: Boolean
)