import { fetchFromApi } from ".";


export interface ScheduleTable {
    table: (number | null)[][];
}

export interface TimeSlotBounceRate {
    onlyDefault: number;
    withAlt: number;
}
export interface BounceRateCalculation {
    result: TimeSlotBounceRate[];
}


export async function getTable() : Promise<ScheduleTable> {
    let response = await fetchFromApi(`/api/v1/scheduleTable`);
    return await response.json();
}

export async function setDefaultStreamSchedule(slotId: number, categoryId: number) {
    await fetchFromApi(`/api/v1/scheduleTable/default?slotId=${slotId}&categoryId=${categoryId}`, {
        method: 'POST'
    });
}

export async function deleteDefaultStreamSchedule(slotId: number) {
    await fetchFromApi(`/api/v1/scheduleTable/default?slotId=${slotId}`, {
        method: 'DELETE'
    });
}