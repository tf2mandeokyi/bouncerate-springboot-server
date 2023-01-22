import { fetchFromApi } from ".";


export interface AdvertisementProduct {
    id: number;
    name: string;
    availability: boolean;
    bounceRateScore: number;
    scoreUpdatedDate: number;
}


export async function getProduct(id: number) : Promise<AdvertisementProduct> {
    let response = await fetchFromApi(`/api/v1/products/${id}`);
    return await response.json();
}


export async function getPriority() : Promise<AdvertisementProduct[]> {
    let response = await fetchFromApi(`/api/v1/products/getPriority`);
    return await response.json();
}


export async function getProductsPage(count: number, pageNum: number) : Promise<AdvertisementProduct[]> {
    let response = await fetchFromApi(`/api/v1/products?count=${count}&page=${pageNum}`);
    return await response.json();
}


export async function getProductsCount() : Promise<number> {
    let response = await fetchFromApi(`/api/v1/products/count`);
    return await response.json();
}