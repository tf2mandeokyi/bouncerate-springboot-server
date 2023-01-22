import React, { useCallback, useEffect, useState } from 'react'
import { AdvertisementProduct, getPriority } from '../../api/products';
import ProductRankingTable from './table';

import './index.css'


const ProductRanking : React.FC = () => {

    const [ productRankingList, setProductRankingList ] = useState<AdvertisementProduct[]>([]);


    const fetchRankingList = useCallback(async () => {
        setProductRankingList(await getPriority())
    }, [ setProductRankingList ])


    useEffect(() => {
        fetchRankingList();
    }, [ fetchRankingList ])


    return <div className='product-ranking'>
        <div className='ranking-title'>광고 송출 목록</div>
        <ProductRankingTable rankingList={ productRankingList } />
    </div>
}

export default ProductRanking