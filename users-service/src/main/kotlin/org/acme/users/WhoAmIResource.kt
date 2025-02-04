package org.acme.users

import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.SecurityContext

@Path("/whoami")
class WhoAmIResource {
    @Inject
    lateinit var whoami: Template
    @Inject
    lateinit var securityContext: SecurityContext

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun get():TemplateInstance {
        val userId = securityContext.userPrincipal?.name
        return whoami.data("name", userId)
    }
}