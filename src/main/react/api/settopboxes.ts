import { fetchFromApi } from ".";


export interface SetTopBox {
    id: number;
    name: string;
}


export async function getSetTopBoxesPage(count: number, pageNum: number) : Promise<SetTopBox[]> {
    let response = await fetchFromApi(`/api/v1/setTopBoxes?count=${count}&page=${pageNum}`);
    return await response.json();
}


export async function getSetTopBoxesCount() : Promise<number> {
    let response = await fetchFromApi(`/api/v1/setTopBoxes/count`);
    return await response.json();
}