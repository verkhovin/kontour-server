/*
 * Kontour Server
 * Copyright (C) 2019  Nikita Verkhovin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.kontour.server.service.mail

import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class MailNotificationService(
    private val config: SmtpConfig
) {
    fun send(emailAddress: String, mail: Mail) {
        val message = buildMessage(emailAddress, mail)
        Transport.send(message)
    }

    private fun buildMessage(emailAddress: String, mail: Mail): Message {
        val properties = System.getProperties().apply {
            put("mail.smtp.host", config.smtpHost)
            put("mail.smtp.port", config.smtpPort.toString())
            put("mail.smtp.auth", "true")
        }

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(config.username, config.password)
            }
        })

        return MimeMessage(session).apply {
            setFrom(InternetAddress(config.mailFrom))
            setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress, false))
            subject = mail.subject
            MimeMultipart().apply {
                addBodyPart(MimeBodyPart().apply {
                    setContent(mail.text, "text/html")
                })
            }

        }
    }
}

data class Mail(val subject: String, val text: String)
data class SmtpConfig(val smtpHost: String, val smtpPort: Int, val username: String, val password: String, val mailFrom: String)