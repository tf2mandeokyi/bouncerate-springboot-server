import React, { useEffect, useState } from 'react'
import { ProductCategory } from '../../../api/categories';
import NameAndId from '../../../components/name-and-id';


type Props = {
    rankingList?: ProductCategory[]
}

const CategoryRankingTable : React.FC<Props> = (props) => {

    const [ tableRows, setTableRows ] = useState<JSX.Element[] | undefined>(undefined);

    
    useEffect(() => {
        if(!props.rankingList) return;

        let result : JSX.Element[] = [];
        for(let i = 0; i < props.rankingList.length; i++) {
            let category = props.rankingList[i];
            result.push(
                <tr>
                    <td>대체 광고 #{ i + 1 }: </td>
                    <td><NameAndId entity={ category } /></td>
                    <td>{ category.bounceRateScore }</td>
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

export default CategoryRankingTable