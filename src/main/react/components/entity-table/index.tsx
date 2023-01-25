import { useCallback, useEffect, useState } from 'react'
import NameAndId from '../name-and-id'
import EntityTablePageNumbers from './page-numbers'

import './index.scss'
import { ArrayOrSelf, PromiseOrSelf, SupplierOrSelf } from '../../utils/types';


type Entity = { id: number, name: string };
export type EntityToJSXFunction<T extends Entity> = (entity: T, update: () => void) => PromiseOrSelf<ArrayOrSelf<JSX.Element>>;

    
type Props<T extends Entity> = {
    mode?: any;
    entityNameColumnHead: SupplierOrSelf<ArrayOrSelf<string>>;
    getEntityCount: () => Promise<number>;
    getEntitiesPage: (elementPerPage: number, pageNumber: number) => Promise<T[]>;
    entityToJSX: EntityToJSXFunction<T>
}


const EntityTable = <T extends Entity>({ 
    mode, entityNameColumnHead, getEntityCount, getEntitiesPage, entityToJSX 
}: Props<T>) => {

    let columnHeads = typeof entityNameColumnHead === 'function' ? entityNameColumnHead() : entityNameColumnHead;
    if(!(columnHeads instanceof Array)) columnHeads = [ columnHeads ];
    const columnHeadCells = columnHeads.map(c => <td>{ c }</td>)

    const [ doUpdate, setDoUpdate ] = useState<boolean>(false);
    const [ pageNumber, setPageNumber ] = useState<number>(1);
    const [ entityCount, setEntityCount ] = useState<number>(0);
    const [ tableRows, setTableRows ] = useState<JSX.Element[] | undefined>(undefined);

    const ELEMENT_PER_PAGE = 5;


    const update = useCallback(() => {
        setDoUpdate(true);
    }, []);


    const getEntities = useCallback(async () => {
        return {
            count: await getEntityCount(), 
            list: await getEntitiesPage(ELEMENT_PER_PAGE, pageNumber) 
        };
    }, [ pageNumber, getEntityCount, getEntitiesPage ]);


    const setTable = useCallback(async () => {
        let { count, list } = await getEntities();
        if (doUpdate) setDoUpdate(false);

        setEntityCount(count);
        
        let result : JSX.Element[] = [];
        for(let element of list) {
            let jsx = entityToJSX(element, update);
            if(jsx instanceof Promise) jsx = await jsx;
            if(!(jsx instanceof Array)) jsx = [ jsx ];

            result.push(
                <tr>
                    <td><NameAndId entity={ element } /></td>
                    { jsx.map(j => <td>{ j }</td>) }
                </tr>
            )
        }
        setTableRows(result);

    }, [ getEntities, entityToJSX, setEntityCount, update, doUpdate ])


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
                    <tr>{ columnHeadCells }</tr>
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