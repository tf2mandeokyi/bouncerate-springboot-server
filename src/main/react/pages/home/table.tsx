import React, { useCallback, useState } from 'react'
import EntityTable, { EntityToJSXFunction, TableHeadColumns } from '../../components/entity-table';
import { addProduct, AdvertisementProduct, deleteProduct, getProductsCount, getProductsPage } from '../../api/products';
import { addSetTopBox, deleteSetTopBox, getSetTopBoxesCount, getSetTopBoxesPage, SetTopBox } from '../../api/settopboxes';

import './index.scss'
import { ArrayOrSelf } from '../../utils/types';


interface MainPageTableModeButtonProps {
    highlight: boolean; 
    children: React.ReactNode;
    callback: () => void;
}
const MainPageTableModeButton : React.FC<MainPageTableModeButtonProps> = (props) => {

    return (
        <div 
            className={ `button big blue ${ props.highlight ? 'highlighted' : '' }` }
            onClick={ props.callback }
        >
            { props.children }
        </div>
    )
}


export enum Mode {
    PRODUCTS, SETTOPBOXES
}
const MainPageTableDiv : React.FC = () => {

    const [ mode, setMode ] = useState<Mode>(Mode.PRODUCTS);
    const setModeAsProducts = useCallback(() => setMode(Mode.PRODUCTS), [ setMode ])
    const setModeAsSetTopBoxes = useCallback(() => setMode(Mode.SETTOPBOXES), [ setMode ])


    const addEntity = useCallback(async (update: () => void) => {
        let promptInput = prompt(`추가할 ${ mode === Mode.PRODUCTS ? '광고 상품' : '셋톱박스' }의 이름을 입력해주세요.`)
        if(!promptInput) return;

        mode === Mode.PRODUCTS ? 
            await addProduct({ name: promptInput, availability: true }) : 
            await addSetTopBox({ name: promptInput });
        update();
    }, [ mode ]);


    const entityToJSX : EntityToJSXFunction<AdvertisementProduct | SetTopBox> = useCallback(async ({ id }, update) => {
        return [
            <div key={ `${id}-delete` }
                className='button red' 
                onClick={ async () => { 
                    mode === Mode.PRODUCTS ? await deleteProduct(id) : await deleteSetTopBox(id);
                    update() 
                } }
            >삭제</div>,
            <div key={ `${id}-info` }
                className='button darkblue'
                onClick={ () => { 
                    window.location.href = `/${mode === Mode.PRODUCTS ? 'products' : 'setTopBoxes'}?id=${id}` 
                } }
            >정보</div>
        ]
    }, [ mode ]);


    const getTableHeadColumn : (update: () => void) => TableHeadColumns = useCallback((update) => [
        <>{ mode === Mode.PRODUCTS ? '광고 상품 이름' : '셋톱박스 이름' }</>, 
        [
            <div className='button blue' onClick={ () => addEntity(update) }>추가하기</div>, 
            2
        ]
    ], [ mode, addEntity ]);


    const productTable = (
        <EntityTable<AdvertisementProduct>
            mode={ mode }
            tableHeadColumn={ getTableHeadColumn }
            getEntityCount={ getProductsCount }
            getEntitiesPage={ getProductsPage }
            entityToJSX={ entityToJSX } 
        />
    )
    const setTopBoxTable = (
        <EntityTable<SetTopBox>
            mode={ mode }
            tableHeadColumn={ getTableHeadColumn }
            getEntityCount={ getSetTopBoxesCount }
            getEntitiesPage={ getSetTopBoxesPage }
            entityToJSX={ entityToJSX } 
        />
    )


    return <div className='main-page-table-div'>
        <div className='buttons'>
            <MainPageTableModeButton 
                highlight={ mode === Mode.PRODUCTS }
                callback={ setModeAsProducts }
            >
                광고 상품
            </MainPageTableModeButton>
            <MainPageTableModeButton 
                highlight={ mode === Mode.SETTOPBOXES }
                callback={ setModeAsSetTopBoxes }
            >
                셋톱박스
            </MainPageTableModeButton>
        </div>
        { mode === Mode.PRODUCTS ? productTable : setTopBoxTable }
        
    </div>
}

export default MainPageTableDiv