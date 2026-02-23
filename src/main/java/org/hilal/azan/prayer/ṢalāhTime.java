/*
    Copyright (C) 2021-22 Helal Anwar

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licens
 */
package org.hilal.azan.prayer;



import org.hilal.azan.prayer.prayer_Enums.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.chrono.HijrahDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ṢalāhTime extends PrayerTime {
    /**
     * @author Helal Anwar
     * @see Prayers
     * @see ṢalāhTime
     * @see HijrahDate
     * @see Time
     * @see Institution
     * @see IslamicMonths
     * @see IslamicWeek
     * @see Method
     */
    public ṢalāhTime(double latitude, double longitude, TimeZones timeZones, Institution institution) {
        super(latitude, longitude, timeZones, institution);
    }

    public ṢalāhTime() {
        super();
    }

    public LocalTime FajirTime() {
        return Time.formatTime(getFajirTime());
    }

    public LocalTime DuhurTime() {
        return Time.formatTime(getDuhurTime());
    }


    public LocalTime AsrTime() {
        return Time.formatTime(getAsrTime());
    }

    public LocalTime IshaTime() {
        if (getInstitution().equals(Institution.Umm_Al_Qura_University_Mecca) && getIslamicMonth().equals(IslamicMonths.Ramadan.getMonthName()))
            return Time.formatTime(getMaghribTime() + (double) 120 / 60);
        return Time.formatTime(getIshaTime());
    }

    public LocalTime MaghribTime() {
        return Time.formatTime(getMaghribTime());
    }

    public LocalTime JummahTime() {
        return Time.add(Time.formatTime(getDuhurTime()), 1, 0);
    }

    public LocalTime TahajjudTime() {
        long[] k = Time.TimeDifference(Time.formatTime(getMaghribTime()),
                Time.formatTime(getFajirTime()));
        double x = ((double) Math.abs(k[0])) / 2;
        x = (x - Math.floor(x)) * 60;
        int m = (int) (Math.abs(k[1]) + (int) x);
        return Time.add(Time.formatTime(getMaghribTime()), Math.abs(k[0] / 2), Math.abs(m));
    }

    public  LinkedHashMap<Prayers,LocalTime> allFivePrayers() {
        var x=new LinkedHashMap<Prayers,LocalTime>();
        if (getDate().getDayOfWeek().equals(DayOfWeek.FRIDAY)){
            x.put(Prayers.Fajir,FajirTime());
            x.put(Prayers.Duhur,DuhurTime());
            x.put(Prayers.Jumuah,JummahTime());
            x.put(Prayers.Asr,AsrTime());
            x.put(Prayers.Maghrib,MaghribTime());
            x.put(Prayers.Isha,IshaTime());
            x.put(Prayers.Tahajjud,TahajjudTime());
        }
        else{
            x.put(Prayers.Fajir,FajirTime());
            x.put(Prayers.Duhur,DuhurTime());
            x.put(Prayers.Asr,AsrTime());
            x.put(Prayers.Maghrib,MaghribTime());
            x.put(Prayers.Isha,IshaTime());
            x.put(Prayers.Tahajjud,TahajjudTime());
        }
        return x;
    }
    public  LinkedHashMap<Prayers,String> allFivePrayers_In12HourFormat() {
        var x=new LinkedHashMap<Prayers,String>();
        if (getDate().getDayOfWeek().equals(DayOfWeek.FRIDAY)){
            x.put(Prayers.Fajir,FajirTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Duhur,DuhurTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Jumuah,JummahTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Asr,AsrTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Maghrib,MaghribTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Isha,IshaTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Tahajjud,TahajjudTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        }
        else{
            x.put(Prayers.Fajir,FajirTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Duhur,DuhurTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Asr,AsrTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Maghrib,MaghribTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Isha,IshaTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            x.put(Prayers.Tahajjud,TahajjudTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
        }
        return x;
    }
    public TreeMap<Prayers, Double> allFivePrayersHours()
    {
        return new TreeMap<>(Map.of(Prayers.Fajir, getFajirTime(),
                Prayers.Duhur, getDuhurTime(), Prayers.Asr, getAsrTime(),
                Prayers.Maghrib, getMaghribTime(), Prayers.Isha, getIshaTime()));
    }

    public TreeMap<LocalDate, Map<Prayers, LocalTime>> getPrayerFrom(LocalDate from, LocalDate till) {
        TreeMap<LocalDate, Map<Prayers, LocalTime>> val = new TreeMap<>();
        for (LocalDate i = from; !i.equals(till); i = i.plusDays(1)) {
            this.setDate(i);
            val.put(i, allFivePrayers());
        }
        return val;
    }
    public TreeMap<LocalDate, Map<Prayers, String>> getPrayerFrom_In12HourFormat(LocalDate from, LocalDate till) {
        TreeMap<LocalDate, Map<Prayers, String>> val = new TreeMap<>();
        for (LocalDate i = from; !i.equals(till); i = i.plusDays(1)) {
            this.setDate(i);
            val.put(i, allFivePrayers_In12HourFormat());
        }
        return val;
    }
    public TreeMap<LocalDate, Map<Prayers, Double>> getPrayerFromInHours(LocalDate from, LocalDate till) {
        TreeMap<LocalDate, Map<Prayers, Double>> val = new TreeMap<>();
        while (!from.plusDays(1).equals(till)) {
            this.setDate(from);
            val.put(from, this.allFivePrayersHours());
            from = from.plusDays(1);
        }
        return val;
    }

    public long[] getPrayerTimeDifference(Prayers prayer1, Prayers prayer2) {
        long[] diff_time = Time.TimeDifference(getT(prayer1), getT(prayer2));
        return new long[]{Math.abs(diff_time[0]), Math.abs(diff_time[1])};
    }
    public long[] getPrayerTimeDifference(Prayers prayer,LocalTime localTime){
        if ((prayer.equals(Prayers.Fajir)||prayer.equals(Prayers.Tahajjud))&&localTime.format(DateTimeFormatter.ofPattern("hh:mm a")).contains("PM")){
            long []diff_time1=Time.TimeDifference(localTime,LocalTime.parse("23:59"));
            long []diff_time2=Time.TimeDifference(LocalTime.parse("00:00"),getT(prayer));
            int  total_minutes= (int) (diff_time1[1]+diff_time2[1]);
            int total_hours=(int) (diff_time1[0]+diff_time2[0]);
            return new long[]{total_hours+total_minutes/60,total_minutes%60+1};
        }
        long[] diff_time = Time.TimeDifference(getT(prayer), localTime);
        return new long[]{Math.abs(diff_time[0]), Math.abs(diff_time[1])};
    }
    public String getIslamicWeekDays() {
        TreeMap<Integer, String> x = IntStream.range(0, 7).boxed().
                collect(Collectors.toMap(i -> i + 1, i ->
                        IslamicWeek.values()[i].getDayName(), (a, b) -> b, TreeMap::new));
        return x.get(getDate().getDayOfWeek().getValue());
    }

    public String getIslamicMonth() {
        TreeMap<Integer, String> x = IntStream.range(0, 12).boxed().
                collect(Collectors.toMap(i -> i + 1, i -> IslamicMonths.values()[i].getMonthName(), (a, b) -> b, TreeMap::new));
        return x.get(getIslamicMonthValue());
    }

    private   LocalTime getT(Prayers p1) {
        return switch (p1) {
            case Fajir -> FajirTime();
            case Duhur -> DuhurTime();
            case Asr -> AsrTime();
            case Jumuah -> JummahTime();
            case Tahajjud -> TahajjudTime();
            case Maghrib -> MaghribTime();
            case Isha -> IshaTime();
            default -> throw new IllegalArgumentException();
        };
    }

    public Prayers getCurrentPrayer() {
        LocalTime localTime=LocalTime.now();
        if(localTime.isBefore(DuhurTime()) && !localTime.isBefore(FajirTime()))
            return Prayers.Fajir;
        if(localTime.isBefore(AsrTime()) && !localTime.isBefore(DuhurTime())){
            if (getDate().getDayOfWeek().equals(DayOfWeek.FRIDAY)){
                if (localTime.isBefore(JummahTime()) && !localTime.isBefore(DuhurTime()))
                    return Prayers.Jumuah;
                else return Prayers.Duhur;
            }
            else return Prayers.Duhur;
        }
        if(localTime.isBefore(MaghribTime()) && !localTime.isBefore(AsrTime()))
            return Prayers.Asr;
        if(localTime.isBefore(IshaTime()) && !localTime.isBefore(MaghribTime()))
            return Prayers.Maghrib;
        if(localTime.isBefore(TahajjudTime()) && !localTime.isBefore(IshaTime()))
            return Prayers.Isha;
        if(IshaTime().isAfter(TahajjudTime())){
            if (!localTime.isAfter(LocalTime.parse("23:59")))
                return Prayers.Isha;
            if (localTime.equals(LocalTime.parse("00:00")))
                return Prayers.Isha;
            if (localTime.isAfter(LocalTime.parse("00:00")) && !localTime.isAfter(TahajjudTime()))
                return Prayers.Isha;
        }
        if (IshaTime().isBefore(TahajjudTime())){
            if(localTime.isBefore(LocalTime.parse("23:59")) && !localTime.isBefore(TahajjudTime()))
                return Prayers.Tahajjud;
            if(localTime.isBefore(FajirTime()) && !localTime.isBefore(LocalTime.parse("00:00")))
                return Prayers.Tahajjud;
        }
        if(localTime.isBefore(FajirTime()) && !localTime.isBefore(TahajjudTime()))
            return Prayers.Tahajjud;
        return null;
    }
    public Prayers getNextPrayer(){
        LocalTime localTime=LocalTime.parse(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        if(localTime.isBefore(DuhurTime()) && !localTime.isBefore(FajirTime()))
            return Prayers.Duhur;
        if(localTime.isBefore(AsrTime()) && !localTime.isBefore(DuhurTime())){
            if (getDate().getDayOfWeek().equals(DayOfWeek.FRIDAY)){
                if (localTime.isBefore(JummahTime()) && !localTime.isBefore(DuhurTime()))
                    return Prayers.Jumuah;
                else return Prayers.Asr;
            }
            else return Prayers.Asr;
        }
        if(localTime.isBefore(MaghribTime()) && !localTime.isBefore(AsrTime()))
            return Prayers.Maghrib;
        if(localTime.isBefore(IshaTime()) && !localTime.isBefore(MaghribTime()))
            return Prayers.Isha;
        if(localTime.isBefore(TahajjudTime()) && !localTime.isBefore(IshaTime()))
            return Prayers.Tahajjud;
        if(IshaTime().isAfter(TahajjudTime())){
            if (!localTime.isAfter(LocalTime.parse("23:59")) && localTime.format(DateTimeFormatter.ofPattern("hh:mm a")).contains("PM"))
                return Prayers.Tahajjud;
            if (localTime.equals(LocalTime.parse("00:00")))
                return Prayers.Tahajjud;
            if (localTime.isAfter(LocalTime.parse("00:00")) && !localTime.isAfter(TahajjudTime()))
                return Prayers.Tahajjud;
        }
        if (IshaTime().isBefore(TahajjudTime())){
            if(localTime.isBefore(LocalTime.parse("23:59")) && !localTime.isBefore(TahajjudTime()))
                return Prayers.Fajir;
            if(localTime.isBefore(FajirTime()) && !localTime.isBefore(LocalTime.parse("00:00")))
                return Prayers.Fajir;
        }
        if(localTime.isBefore(FajirTime()) && !localTime.isBefore(TahajjudTime()))
            return Prayers.Fajir;
        return null;
    }
    public HijrahDate getIslamicDateNow() {
        return LocalTime.now().getHour() > Math.floor(getMaghribTime()) ?
                HijrahDate.from(getDate().plusDays(1)) : HijrahDate.from(getDate());
    }

    public HijrahDate getIslamicDate() {
        return HijrahDate.from(getDate());
    }

    public String getIslamicYear() {
        String x = HijrahDate.from(getDate()).toString();
        return x.substring(x.lastIndexOf(' ') + 1);
    }

    public int getIslamicMonthValue() {
        return Integer.parseInt(getIslamicYear().substring(getIslamicYear().indexOf('-') + 1, getIslamicYear().lastIndexOf('-')));
    }
}
