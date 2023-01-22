const BOUNCERATE_API_SERVER = process.env['REACT_APP_BOUNCERATE_API_SERVER_LINK'] ?? ''

export async function fetchFromApi(input: RequestInfo | URL, init?: RequestInit | undefined) {
    return await fetch(`${BOUNCERATE_API_SERVER}${input}`, init);
}