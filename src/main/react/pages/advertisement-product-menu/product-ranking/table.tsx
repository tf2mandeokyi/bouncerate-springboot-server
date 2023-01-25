import React, { useEffect, useState } from 'react'
import { AdvertisementProduct } from '../../../api/products';
import NameAndId from '../../../components/name-and-id';


type Props = {
    rankingList?: AdvertisementProduct[]
}

const ProductRankingTable : React.FC<Props> = (props) => {

    const [ tableRows, setTableRows ] = useState<JSX.Element[] | undefined>(undefined);

    
    useEffect(() => {
        if(!props.rankingList) return;

        let result : JSX.Element[] = [];
        for(let i = 0; i < props.rankingList.length; i++) {
            let product = props.rankingList[i];
            result.push(
                <tr>
                    <td>대체 광고 #{ i + 1 }: </td>
                    <td><NameAndId entity={ product } /></td>
                    <td>{ product.bounceRateScore }</td>
                </tr>
            )
        }
        setTableRows(result);
    }, [ props.rankingList ])


    return tableRows ? (
        <table>
            <thead>
                <tr>
                    <td></td>
                    <td>광고 이름</td>
                    <td>Bouncerate 점수</td>
                </tr>
            </thead>
            <tbody>
                { tableRows }
            </tbody>
        </table>
    ) : <>Loading...</>
}

export default ProductRankingTable