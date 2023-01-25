import { fetchFromApi } from "."

type NumbersOnlyObject = { [x: string]: number }

export async function getBounceRate({ productId, setTopBoxId }: NumbersOnlyObject) : Promise<number> {
    let response = await fetchFromApi(`/api/v1/bounceRates/product/${productId}/${setTopBoxId}`);
    return await response.json();
}

export async function setBounceRate({ productId, setTopBoxId }: NumbersOnlyObject, bounceRate: number) : Promise<number> {
    let response = await fetchFromApi(`/api/v1/bounceRates/product/${productId}/${setTopBoxId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(bounceRate)
    });
    return await response.json();
}