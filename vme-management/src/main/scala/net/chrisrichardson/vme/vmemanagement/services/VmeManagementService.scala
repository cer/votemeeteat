package net.chrisrichardson.vme.vmemanagement.services

import net.chrisrichardson.vme.vmemanagement.messages.CreateVmeRequest


trait VmeManagementService {

    def createVme(request : CreateVmeRequest)
    
}