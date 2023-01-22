import React, { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { AdvertisementProduct, getProduct } from '../../api/products';



const AdvertisementProductMenu : React.FC = (props) => {

    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const productId = parseInt(params.get('id') ?? '-1');

    const [ product, setProduct ] = useState<AdvertisementProduct>();

    useEffect(() => {
        (async () => {
            setProduct(await getProduct(productId));
        })();
    }, [ productId, setProduct ]);

    return (
        <>
            { product?.name ?? "Loading..." }
            { product?.availability }
        </>
    )
    
}

export default AdvertisementProductMenu;