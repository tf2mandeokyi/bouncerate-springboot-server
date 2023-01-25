import React, { useCallback, useEffect, useRef, useState } from 'react'
import EntityTable, { EntityToJSXFunction } from '../../components/entity-table';
import { AdvertisementProduct, deleteProduct, getProductsCount, getProductsPage } from '../../api/products';
import { deleteSetTopBox, getSetTopBoxesCount, getSetTopBoxesPage, SetTopBox } from '../../api/settopboxes';


interface MainPageTableModeButtonProps {
    highlight: boolean; 
    children: React.ReactNode;
    callback: () => void;
}
const MainPageTableModeButton : React.FC<MainPageTableModeButtonProps> = (props) => {

    const thisRef = useRef<HTMLDivElement>(null);

    useEffect(() => {
        let thisDiv = thisRef.current;
        thisDiv?.addEventListener('click', props.callback);
        return () => { 
            thisDiv?.removeEventListener('click', props.callback) 
        }
    }, [ props.callback ])

    return (
        <div 
            className={ `button main-page-table-mode-button ${ props.highlight ? 'highlighted' : '' }` }
            ref={ thisRef }
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


    const entityToJSX : EntityToJSXFunction<AdvertisementProduct | SetTopBox> = useCallback(async ({ id }, update) => {
        return [
            <div 
                className='button delete' 
                onClick={ async () => { 
                    mode === Mode.PRODUCTS ? await deleteProduct(id) : await deleteSetTopBox(id);
                    update() 
                } }
            >삭제</div>,
            <div 
                className='button info'
                onClick={ () => { 
                    window.location.href = `/${mode === Mode.PRODUCTS ? 'products' : 'setTopBoxes'}?id=${id}` 
                } }
            >정보</div>
        ]
    }, [ mode ]);


    return <div className='main-page-table-div'>
        <div className='main-page-table-mode-buttons'>
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
        { mode === Mode.PRODUCTS ? 
            <EntityTable<AdvertisementProduct>
                mode={ mode }
                entityNameColumnHead='광고 상품 이름'
                getEntityCount={ async () => await getProductsCount() }
                getEntitiesPage={ async (e, p) => getProductsPage(e, p) }
                entityToJSX={ entityToJSX } 
            /> : 
            <EntityTable<SetTopBox>
                mode={ mode }
                entityNameColumnHead='셋톱박스 이름'
                getEntityCount={ async () => await getSetTopBoxesCount() }
                getEntitiesPage={ async (e, p) => getSetTopBoxesPage(e, p) }
                entityToJSX={ entityToJSX } 
            />
        }
        
    </div>
}

export default MainPageTableDiv