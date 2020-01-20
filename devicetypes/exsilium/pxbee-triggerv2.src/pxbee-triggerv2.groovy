/**
 *	Copyright 2020 Sten Feldman
 */

metadata {
  definition (name: "PXBee TriggerV2", namespace: "exsilium", author: "Sten Feldman", ocfDeviceType: "oic.d.door", runLocally: true, minHubCoreVersion: '000.019.00012', executeCommandsLocally: true, genericHandler: "Zigbee") {
    capability "Door Control"
    capability "Contact Sensor"
    capability "Refresh"
    capability "Health Check"
    
    command "openPedestrian"

    fingerprint profileId: "0104", inClusters: "0000, 0003, 0006", manufacturer: "PXBee", model: "Trigger", deviceJoinName: "TriggerV2 WIP Gate Opener"
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
    standardTile("pedestrian", "device.door", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
      state "default", label:'Pedestrian', action:"openPedestrian", icon:"st.Health & Wellness.health12"
    }   
    standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
      state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
    }
    standardTile("r1", "device.r1", decoration: "flat", width: 1, height: 1) {
      state "default", label:"R1", icon:"st.Health & Wellness.health9"
      state "trigger", label:"TRIGGER", icon:"st.motion.motion.active", backgroundColor:"#00A0DC"
      state "exec", label:"EXEC", icon:"st.motion.motion.active", backgroundColor:"#e86d13"
    }
    standardTile("r2", "device.r2", decoration: "flat", width: 1, height: 1) {
      state "default", label:"R1", icon:"st.Health & Wellness.health9"
      state "trigger", label:"TRIGGER", icon:"st.motion.motion.active", backgroundColor:"#00A0DC"
      state "exec", label:"EXEC", icon:"st.motion.motion.active", backgroundColor:"#e86d13"
    }
    standardTile("r3", "device.r3", decoration: "flat", width: 1, height: 1) {
      state "default", label:"R1", icon:"st.Health & Wellness.health9"
      state "trigger", label:"TRIGGER", icon:"st.motion.motion.active", backgroundColor:"#00A0DC"
      state "exec", label:"EXEC", icon:"st.motion.motion.active", backgroundColor:"#e86d13"
    }
    standardTile("r4", "device.r4", decoration: "flat", width: 1, height: 1) {
      state "default", label:"R1", icon:"st.Health & Wellness.health9"
      state "trigger", label:"TRIGGER", icon:"st.motion.motion.active", backgroundColor:"#00A0DC"
      state "exec", label:"EXEC", icon:"st.motion.motion.active", backgroundColor:"#e86d13"
    }
    main "toggle"
    details(["toggle", "open", "close", "pedestrian", "refresh", "r1", "r2", "r3", "r4"])
  }
}

// Parse incoming device messages to generate events
def parse(String description) {
  Map eventMap = zigbee.getEvent(description)
  Map eventDescMap = zigbee.parseDescriptionAsMap(description)

  if (!eventMap && eventDescMap) {
    eventMap = [:]
    if (eventDescMap?.clusterInt == zigbee.ONOFF_CLUSTER) {
      if(eventDescMap?.sourceEndpoint == "EA") {
	    eventMap["name"] = "r1"
      }
      else if (eventDescMap?.sourceEndpoint == "EB") {
        eventMap["name"] = "r2"
      }
      else if (eventDescMap?.sourceEndpoint == "EC") {
        eventMap["name"] = "r3"
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
    else {
      log.warn "DID NOT PARSE MESSAGE for description : $description"
      log.debug "eventDescMap: $eventDescMap"
    }
  }

  if (eventMap) {
    sendEvent(eventMap)
  }
}

def open() {
  zigbee.command(zigbee.ONOFF_CLUSTER, 0x01, "", [destEndpoint: 0xEA]) + sendEvent(name: "r1", value: "trigger") + runIn(6, finishOpening)
}

def close() {
  zigbee.command(zigbee.ONOFF_CLUSTER, 0x01, "", [destEndpoint: 0xEB]) + sendEvent(name: "r2", value: "trigger") + runIn(6, finishClosing)
}

def openPedestrian() {
  zigbee.command(zigbee.ONOFF_CLUSTER, 0x01, "", [destEndpoint: 0xEC]) + sendEvent(name: "r3", value: "trigger")
}

def finishOpening() {
  sendEvent(name: "door", value: "open")
  sendEvent(name: "contact", value: "open")
}

def finishClosing() {
  sendEvent(name: "door", value: "closed")
  sendEvent(name: "contact", value: "closed")
}

/**
 * PING is used by Device-Watch in attempt to reach the Device
 **/
def ping() {
  return refresh()
}

def refresh() {
  // readAttribute(ONOFF_CLUSTER, 0x0000)
  zigbee.onOffRefresh() + zigbee.onOffConfig()
}

def configure() {
  // Device-Watch allows 2 check-in misses from device + ping (plus 2 min lag time)
  sendEvent(name: "checkInterval", value: 2 * 10 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
  log.debug "Configuring Reporting and Bindings."
  zigbee.onOffRefresh() + zigbee.onOffConfig()
}