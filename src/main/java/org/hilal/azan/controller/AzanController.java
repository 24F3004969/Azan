package org.hilal.azan.controller;


import org.hilal.azan.prayer.Prayers;
import org.hilal.azan.prayer.prayer_Enums.Institution;
import org.hilal.azan.prayer.prayer_Enums.TimeZones;
import org.hilal.azan.prayer.ṢalāhTime;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.LinkedHashMap;

@RestController
public class AzanController {
    ṢalāhTime time = new ṢalāhTime(22.805618,
            86.2029,
            TimeZones.Asia_Kolkata,
            Institution.University_of_Islamic_Science_Karachi);

    @GetMapping(path = "/azan_time", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @CrossOrigin(origins = "*")// ← allow all for development
    public LinkedHashMap<Prayers, String> getData() {
        return time.allFivePrayers_In12HourFormat();
    }
}

