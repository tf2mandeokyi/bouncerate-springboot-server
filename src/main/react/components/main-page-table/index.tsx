import React, { useCallback, useEffect, useRef, useState } from 'react'

import './index.css'
import EntityTable, { EntityToJSXFunction } from '../entity-table';


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
            className={ `main-page-table-mode-button ${ props.highlight ? 'highlighted' : '' }` }
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

    const entityToJSX : EntityToJSXFunction = useCallback((entity) => {
        return <>
            <div className='main-page-table-delete'>삭제</div>
            <div className='main-page-table-info'>정보</div>
        </>
    }, []);

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
        <EntityTable mode={ mode } entityToJSX={ entityToJSX } />
    </div>
}

export default MainPageTableDiv