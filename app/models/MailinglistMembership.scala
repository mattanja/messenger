package models


/**
 * TODO: implement relation between user and mailinglist
 */
case class MailinglistMembership(
    memberemail: String,
    mailinglistemail: String,
    isApproved: Boolean,
    isActive: Boolean
)