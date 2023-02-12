package com.mndk.bouncerate.service;

import com.mndk.bouncerate.db.ScheduleTableDAO;
import com.mndk.bouncerate.db.TemporaryBounceRateCalculationDAO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleTableService {


    private static final int TIME_SLOT_COUNT = 6;
    private static final int ALT_STREAM_COUNT = 3;


    final ScheduleTableDAO scheduleTableDAO;
    final TemporaryBounceRateCalculationDAO temporaryBounceRateCalculationDAO;


    // ===== GETTERS =====

    public Integer[][] getEntireTable() {
        List<ScheduleTableDAO.ScheduleNode> nodes = scheduleTableDAO.getAll();

        int timeSlotCount = TIME_SLOT_COUNT;
        int altStreamCount = ALT_STREAM_COUNT + 1;

        Integer[][] table = new Integer[6][];
        for(int i = 0; i < timeSlotCount; i++) {
            table[i] = new Integer[altStreamCount];
            Arrays.fill(table[i], null);
        }
        for(ScheduleTableDAO.ScheduleNode node : nodes) {
            int timeSlotId = node.getTimeSlotId(), streamNumber = node.getStreamNumber();

            if(timeSlotId < 0 || timeSlotId > timeSlotCount - 1) continue;
            if(streamNumber < 0 || streamNumber > altStreamCount) continue;

            table[timeSlotId][streamNumber] = node.getCategoryId();
        }

        return table;
    }


    // ===== SETTERS =====

    public void setOne(int timeSlotId, int streamNumber, int categoryId) {
        try {
            validateTimeSlot(timeSlotId);
            scheduleTableDAO.insertNode(new ScheduleTableDAO.ScheduleNode(timeSlotId, streamNumber, categoryId));
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }


    // ===== REMOVERS =====

    public void removeOne(int timeSlotId, int streamNumber) {
        validateTimeSlot(timeSlotId);
        scheduleTableDAO.deleteNode(timeSlotId, streamNumber);
    }


    // ===== MISC =====

    public void calculateBounceRateOfTimeSlot(int timeSlotId) {
        synchronized (temporaryBounceRateCalculationDAO) {
            System.out.println("Bouncerate calculation start");

            temporaryBounceRateCalculationDAO.initialize(timeSlotId, 0, 30);
            System.out.println("Initialization done");

            for (int i = 0; i < ALT_STREAM_COUNT; i++) {
                temporaryBounceRateCalculationDAO.loop(timeSlotId, i + 1, 0, 30);
                System.out.println("Loop #" + (i + 1) + " done");
            }
        }
    }


    private void validateTimeSlot(int timeSlotId) {
        if(timeSlotId < 0 || timeSlotId > TIME_SLOT_COUNT - 1) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
    }

}
