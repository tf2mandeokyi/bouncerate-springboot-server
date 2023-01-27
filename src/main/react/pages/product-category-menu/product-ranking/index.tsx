import React, { useCallback, useEffect, useState, useRef } from 'react'
import { ProductCategory, getPriority } from '../../../api/products';
import ProductRankingTable from './table';

import './index.scss'


const ProductRanking : React.FC = () => {

    const [ productRankingList, setProductRankingList ] = useState<ProductCategory[]>([]);
    const rankingCountInputRef = useRef<HTMLInputElement>(null);


    const fetchRankingList = useCallback(async (forceUpdate: boolean) => {
        let count = parseInt(rankingCountInputRef.current?.value ?? '3');
        if(isNaN(count)) count = 3;
        
        setProductRankingList(await getPriority({ forceUpdate, count }))
    }, [ setProductRankingList ])


    useEffect(() => {
        fetchRankingList(false);
    }, [ fetchRankingList ])


    return (
        <div className='product-ranking'>
            <div className='title'>광고 송출 목록</div>
            <CategoryRankingTable rankingList={ productRankingList } />
            <div className='bottom'>
                <div className='count-div'>
                    개수: 
                    <input ref={ rankingCountInputRef } placeholder='개수' type='number'/>
                </div>
                <div 
                    className='button darkblue' 
                    onClick={ () => fetchRankingList(true) }
                >
                    새로고침
                </div>
            </div>
        </div>
    )
}

export default ProductRanking