import React, { useCallback, useEffect, useState } from 'react'
import { Mode } from '../main-page-table'
import { AdvertisementProduct, getProductsCount, getProductsPage } from '../../api/products'
import { getSetTopBoxesCount, getSetTopBoxesPage, SetTopBox } from '../../api/settopboxes'
import NameAndId from '../name-and-id'

import './index.css'
import EntityTablePageNumbers from './page-numbers'


export type EntityToJSXFunction = (entity: { id: number, name: string }) => JSX.Element;

type Props = {
    mode: Mode;
    entityToJSX: EntityToJSXFunction
}

const EntityTable : React.FC<Props> = ({ mode, entityToJSX }) => {

    const [ pageNumber, setPageNumber ] = useState<number>(1);
    const [ entityCount, setEntityCount ] = useState<number>(0);
    const [ tableRows, setTableRows ] = useState<JSX.Element[] | undefined>(undefined);

    const ELEMENT_PER_PAGE = 5;

    const getEntities = useCallback(async () => {
        let count : number, list : (AdvertisementProduct | SetTopBox)[];

        if(mode === Mode.SETTOPBOXES) {
            count = await getSetTopBoxesCount();
            list = await getSetTopBoxesPage(ELEMENT_PER_PAGE, pageNumber);
        } else {
            count = await getProductsCount();
            list = await getProductsPage(ELEMENT_PER_PAGE, pageNumber);
        }

        return { count, list };
    }, [ pageNumber, mode ]);

    const setTable = useCallback(async () => {
        let { count, list } = await getEntities();

        setEntityCount(count);
        
        let result : JSX.Element[] = [];
        for(let element of list) {
            result.push(
                <tr>
                    <td><NameAndId entity={ element } /></td>
                    <td className='entity-table-entitytojsx'>
                        { entityToJSX(element) }
                    </td>
                </tr>
            )
        }
        setTableRows(result);

    }, [ getEntities, entityToJSX, setEntityCount ])


    useEffect(() => {
        setTable();
    }, [ setTable ]);


    useEffect(() => {
        setPageNumber(1);
    }, [ mode ])


    return tableRows ? (
        <div className='entity-table-div'>
            <table className='entity-table'>
                <thead>
                    <tr>
                        <td>{ mode === Mode.PRODUCTS ? '광고 상품 이름' : '셋톱박스 이름' }</td>
                    </tr>
                </thead>
                <tbody>
                    { tableRows }
                </tbody>
            </table>
            <EntityTablePageNumbers 
                min={ 1 } 
                max={ Math.ceil(entityCount / ELEMENT_PER_PAGE) } 
                current={ pageNumber } 
                callback={ setPageNumber }
            />
        </div>
    ) : <>Loading...</>
}

export default EntityTable