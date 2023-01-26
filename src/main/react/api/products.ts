import { fetchFromApi } from ".";


export interface AdvertisementProduct {
    id: number;
    name: string;
    availability: boolean;
    bounceRateScore: number;
}


export async function getProduct(id: number) : Promise<AdvertisementProduct> {
    let response = await fetchFromApi(`/api/v1/products/${id}`);
    return await response.json();
}


export async function addProduct(params: { name: string, availability: boolean }) {
    await fetchFromApi('/api/v1/products', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(params)
    });
}


export async function deleteProduct(id: number) {
    await fetchFromApi(`/api/v1/products/${id}`, {
        method: 'DELETE'
    });
}


export async function getPriority(params?: { 
    count?: number, 
    forceUpdate?: boolean 
}) : Promise<AdvertisementProduct[]> {

    let count = params?.count ?? 3;
    let forceUpdate = params?.forceUpdate ?? false;
    let response = await fetchFromApi(
        `/api/v1/products/getPriority?count=${count}&forceUpdate=${forceUpdate}`
    );
    return await response.json();
}


export async function getProductsPage(count: number, pageNum: number) : Promise<AdvertisementProduct[]> {
    let response = await fetchFromApi(`/api/v1/products?count=${count}&page=${pageNum}`);
    return await response.json();
}


export async function getProductsCount() : Promise<number> {
    let response = await fetchFromApi(`/api/v1/products/count`);
    return (await response.json())['value'];
}