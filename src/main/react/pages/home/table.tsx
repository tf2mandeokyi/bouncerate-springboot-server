import React, { useCallback, useState } from 'react'
import EntityTable, { EntityToJSXFunction, TableHeadColumns } from '../../components/entity-table';
import { addCategory, ProductCategory, deleteCategory, getCategoriesCount, getCategoriesPage } from '../../api/categories';
import { addSetTopBox, deleteSetTopBox, getSetTopBoxesCount, getSetTopBoxesPage, SetTopBox } from '../../api/settopboxes';

import './index.scss'


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
    CATEGORIES, SETTOPBOXES
}
const MainPageTableDiv : React.FC = () => {

    const [ mode, setMode ] = useState<Mode>(Mode.CATEGORIES);
    const setModeAsCategories = useCallback(() => setMode(Mode.CATEGORIES), [ setMode ])
    const setModeAsSetTopBoxes = useCallback(() => setMode(Mode.SETTOPBOXES), [ setMode ])


    const addEntity = useCallback(async (update: () => void) => {
        let promptInput = prompt(`추가할 ${ mode === Mode.CATEGORIES ? '광고 상품' : '셋톱박스' }의 이름을 입력해주세요.`)
        if(!promptInput) return;

        mode === Mode.CATEGORIES ?
            await addCategory({ name: promptInput, availability: true }) :
            await addSetTopBox({ name: promptInput });
        update();
    }, [ mode ]);


    const entityToJSX : EntityToJSXFunction<ProductCategory | SetTopBox> = useCallback(async ({ id }, update) => {
        return [
            <div key={ `${id}-delete` }
                className='button red' 
                onClick={ async () => { 
                    mode === Mode.CATEGORIES ? await deleteCategory(id) : await deleteSetTopBox(id);
                    update() 
                } }
            >삭제</div>,
            <div key={ `${id}-info` }
                className='button darkblue'
                onClick={ () => { 
                    window.location.href = `/${mode === Mode.CATEGORIES ? 'categories' : 'setTopBoxes'}?id=${id}`
                } }
            >정보</div>
        ]
    }, [ mode ]);


    const getTableHeadColumn : (update: () => void) => TableHeadColumns = useCallback((update) => [
        <>{ mode === Mode.CATEGORIES ? '광고 상품 이름' : '셋톱박스 이름' }</>,
        [
            <div className='button blue' onClick={ () => addEntity(update) }>추가하기</div>, 
            2
        ]
    ], [ mode, addEntity ]);


    const categoryTable = (
        <EntityTable<ProductCategory>
            mode={ mode }
            tableHeadColumn={ getTableHeadColumn }
            getEntityCount={ getCategoriesCount }
            getEntitiesPage={ getCategoriesPage }
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
                highlight={ mode === Mode.CATEGORIES }
                callback={ setModeAsCategories }
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
        { mode === Mode.CATEGORIES ? categoryTable : setTopBoxTable }
        
    </div>
}

export default MainPageTableDiv