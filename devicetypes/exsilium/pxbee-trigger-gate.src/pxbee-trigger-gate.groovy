/**
 * BSD 2-Clause License
 *
 * Copyright (c) 2020, Sten Feldman
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
 
import physicalgraph.zigbee.zcl.DataType

metadata {
  definition (name: "PXBee Trigger Gate", namespace: "exsilium", author: "Sten Feldman", runLocally: true, minHubCoreVersion: '000.019.00012', executeCommandsLocally: true, genericHandler: "Zigbee") {
    capability "Door Control"
    capability "Contact Sensor"
    capability "Refresh"
    capability "Health Check"
    
    command "openPedestrian"
    command "sendToggle"

    fingerprint profileId: "0104", inClusters: "0000, 0003, 0006, 000F", manufacturer: "PXBee", model: "Trigger", deviceJoinName: "Trigger WIP Gate Opener"
  }

  simulator {

  }

  tiles(scale: 2) {
    standardTile("toggle", "device.door", width: 4, height: 4) {
      state "closed", label:'${name}', action:"door control.open", icon:"st.Outdoor.outdoor8", backgroundColor:"#00A0DC", nextState:"opening"
      state "open", label:'${name}', action:"door control.close", icon:"st.Transportation.transportation12", backgroundColor:"#ff0000", nextState:"closing"
      state "opening", label:'${name}', action:"door control.close", icon:"st.contact.contact.open", backgroundColor:"#00A0DC", nextState:"closing"
      state "closing", label:'${name}', action:"door control.open", icon:"st.contact.contact.closed", backgroundColor:"#ffffff", nextState:"opening"
    }
      standardTile("open", "device.door", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "default", label:'open', action:"door control.open", icon:"st.contact.contact.open"
    }
    standardTile("close", "device.door", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "default", label:'close', action:"door control.close", icon:"st.contact.contact.closed"
    }
    standardTile("pedestrian", "device.door", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "default", label:'Pedestrian', action:"openPedestrian", icon:"st.Health & Wellness.health12"
    }   
    standardTile("sendToggle", "device.door", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "default", label:"Toggle", action:"sendToggle", icon:"st.motion.motion.active"
    }
    standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
    }
    standardTile("r1", "device.r1", decoration: "flat", width: 1, height: 1) {
      state "default", label:"R1", icon:"st.Health & Wellness.health9"
      state "trigger", label:"TRIGGER", icon:"st.motion.motion.active", backgroundColor:"#00A0DC"
      state "exec", label:"EXEC", icon:"st.motion.motion.active", backgroundColor:"#e86d13"
    }
    standardTile("r2", "device.r2", decoration: "flat", width: 1, height: 1) {
      state "default", label:"R2", icon:"st.Health & Wellness.health9"
      state "trigger", label:"TRIGGER", icon:"st.motion.motion.active", backgroundColor:"#00A0DC"
      state "exec", label:"EXEC", icon:"st.motion.motion.active", backgroundColor:"#e86d13"
    }
    standardTile("r3", "device.r3", decoration: "flat", width: 1, height: 1) {
      state "default", label:"R3", icon:"st.Health & Wellness.health9"
      state "trigger", label:"TRIGGER", icon:"st.motion.motion.active", backgroundColor:"#00A0DC"
      state "exec", label:"EXEC", icon:"st.motion.motion.active", backgroundColor:"#e86d13"
    }
    standardTile("r4", "device.r4", decoration: "flat", width: 1, height: 1) {
      state "default", label:"R4", icon:"st.Health & Wellness.health9"
      state "trigger", label:"TRIGGER", icon:"st.motion.motion.active", backgroundColor:"#00A0DC"
      state "exec", label:"EXEC", icon:"st.motion.motion.active", backgroundColor:"#e86d13"
    }
    standardTile("s1", "device.s1", decoration: "flat", width: 1, height: 1) {
      state "default", label:"S1", icon:"st.Outdoor.outdoor8"
      state "open", label:"OPEN", icon:"st.Transportation.transportation12", backgroundColor:"#ff0000"
    }
    standardTile("s2", "device.s2", decoration: "flat", width: 1, height: 1) {
      state "default", label:"s2", icon:"st.Health & Wellness.health9"
      state "operating", label:'${name}', icon:"st.motion.motion.active", backgroundColor:"##00A0DC"
    }

    main "toggle"
    details(["toggle", "open", "close", "pedestrian", "sendToggle", "refresh", "r1", "r2", "r3", "r4", "s1", "s2"])
  }
}

// Globals
private getBINARY_INPUT_CLUSTER()     { 0x000F }
private getPRESENT_VALUE_ATTRIBUTE()  { 0x0055 }
private getZCL_TYPE_LOGICAL_BOOLEAN() { "10"   }

// Parse incoming device messages to generate events
def parse(String description) {
  Map eventMap = [:]
  Map eventDescMap = zigbee.parseDescriptionAsMap(description)

  if (eventDescMap) {
    log.debug "eventDescMap: $eventDescMap"
    if (eventDescMap?.clusterInt == zigbee.ONOFF_CLUSTER) {
      log.debug "Switch Cluster event received"
      if(eventDescMap?.sourceEndpoint == "EA") {
	    eventMap["name"] = "r1"
      }
      else if (eventDescMap?.sourceEndpoint == "EB") {
        eventMap["name"] = "r2"
      }
      else if (eventDescMap?.sourceEndpoint == "EC") {
        eventMap["name"] = "r3"
      }
      else if (eventDescMap?.sourceEndpoint == "ED") {
        eventMap["name"] = "r4"
      }
      else {
        log.error "Message received from unknown sourceEndpoint: $eventDescMap?.sourceEndpoint"
        return
      }
      
	  if(eventDescMap?.data[1] == "01") {
        eventMap["value"] = "exec"
      }
      else {
        eventMap["value"] = "default"
      }
    }
    else if(eventDescMap?.clusterInt == BINARY_INPUT_CLUSTER) {
      log.debug "Binary Input Cluster event received"
      if(eventDescMap?.attrInt == PRESENT_VALUE_ATTRIBUTE && eventDescMap?.encoding == ZCL_TYPE_LOGICAL_BOOLEAN) {
        if(eventDescMap?.endpoint == "EA") {
	      eventMap["name"] = "s1"
          if(eventDescMap?.value == "01") {
            eventMap["value"] = "open"
            sendEvent(name: "contact", value: "open")
          }
          else {
            eventMap["value"] = "default"
            sendEvent(name: "door", value: "closed")
            sendEvent(name: "contact", value: "closed")
          }
      	}
        else if(eventDescMap?.endpoint == "EB") {
	      eventMap["name"] = "s2"
          if(eventDescMap?.value == "01") {
            eventMap["value"] = "operating"
          }
          else {
            eventMap["value"] = "default"
            
            def switchattr = device.latestValue("contact")
            log.debug "Contact state: $switchattr"
            if(switchattr == "open") {
              sendEvent(name: "door", value: "open")
            }
          }
      	}
        else {
          log.error "Invalid endpoint for Binary Input cluster"
        }
      }
      else {
        log.debug "eventDescMap: $eventDescMap"
      }
    }
    else {
      log.warn "DID NOT PARSE MESSAGE for description : $description"
      log.debug "eventDescMap: $eventDescMap"
    }
  }

  if (eventMap) {
    log.debug "eventMap: $eventMap"
    runIn(1, sendEvent, [data: eventMap]) // To slow things down
  }
}

def open() {
  zigbee.command(zigbee.ONOFF_CLUSTER, 0x01, "", [destEndpoint: 0xEA]) + sendEvent(name: "r1", value: "trigger")
}

def close() {
  zigbee.command(zigbee.ONOFF_CLUSTER, 0x01, "", [destEndpoint: 0xEB]) + sendEvent(name: "r2", value: "trigger")
}

def openPedestrian() {
  zigbee.command(zigbee.ONOFF_CLUSTER, 0x01, "", [destEndpoint: 0xEC]) + sendEvent(name: "r3", value: "trigger")
}

def sendToggle() {
  zigbee.command(zigbee.ONOFF_CLUSTER, 0x01, "", [destEndpoint: 0xED]) + sendEvent(name: "r4", value: "trigger")
}

/**
 * PING is used by Device-Watch in attempt to reach the Device
 **/
def ping() {
  return refresh()
}

def refresh() {
  sendEvent(name: "r1", value: "default", isStateChange: true)
  sendEvent(name: "r2", value: "default", isStateChange: true)
  sendEvent(name: "r3", value: "default", isStateChange: true)
  sendEvent(name: "r4", value: "default", isStateChange: true)
  zigbee.onOffRefresh() +
    zigbee.readAttribute(BINARY_INPUT_CLUSTER, PRESENT_VALUE_ATTRIBUTE, [destEndpoint: 0xEA]) +
    zigbee.readAttribute(BINARY_INPUT_CLUSTER, PRESENT_VALUE_ATTRIBUTE, [destEndpoint: 0xEB])
}

def configure() {
  // Device-Watch allows 2 check-in misses from device + ping (plus 2 min lag time)
  sendEvent(name: "checkInterval", value: 2 * 10 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
  log.debug "Configuring Reporting and Bindings."
  zigbee.onOffRefresh() + zigbee.onOffConfig()
}
