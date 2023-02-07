import React, { useCallback, useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom';
import { getBounceRate, setBounceRate } from '../../api/bouncerate';
import { ProductCategory, getCategoriesCount, getCategoriesPage } from '../../api/categories';
import { getSetTopBox, randomizeBounceRatesOfSetTopBox, SetTopBox } from '../../api/settopboxes';
import BackToHome from '../../components/back-to-home';
import EntityDescriptionTable from '../../components/entity-description';
import EntityTable, { EntityToJSXFunction, TableHeadColumns } from '../../components/entity-table';

const SetTopBoxMenu : React.FC = () => {

    const location = useLocation();
    const params = new URLSearchParams(location.search);
    const setTopBoxId = parseInt(params.get('id') ?? '-1');

    const [ setTopBox, setSetTopBox ] = useState<SetTopBox>();


    const onBounceRateEditButtonClick = useCallback(async (category: ProductCategory, update: () => void) => {
        let newBounceRate = parseFloat(prompt('새로운 Bounce rate 값을 입력해주세요.') as string);
        if(isNaN(newBounceRate)) return;

        await setBounceRate({ categoryId: category.id, setTopBoxId }, newBounceRate);
        update();
    }, [ setTopBoxId ]);


    const entityToJSX : EntityToJSXFunction<ProductCategory> = useCallback(async (category, update) => {
        let bounceRate = await getBounceRate({ categoryId: category.id, setTopBoxId });
        return [ 
            <>{ bounceRate ?? '-' }</>, 
            <div 
                key={ category.id }
                className='button darkblue'
                onClick={ () => { onBounceRateEditButtonClick(category, update) } }
            >
                수정
            </div>
        ]
    }, [ setTopBoxId, onBounceRateEditButtonClick ]);


    const onRandomizeButtonClick = useCallback(async (update: () => void) => {
        let min = parseFloat(prompt('랜덤값의 최솟값을 입력해주세요.') as string);
        if(isNaN(min)) return;

        let max = parseFloat(prompt('랜덤값의 최댓값을 입력해주세요.') as string);
        if(isNaN(max)) return;

        await randomizeBounceRatesOfSetTopBox(setTopBoxId, { min, max });
        update();
    }, [ setTopBoxId ]);


    const getTableHeadColumn : (update: () => void) => TableHeadColumns = useCallback((update) => [
        <>광고 상품 이름</>,
        <>Bounce rate</>,
        <div
            key='randomize'
            className='button blue'
            onClick={ () => onRandomizeButtonClick(update) }
        >
            랜덤화
        </div>
    ], [ onRandomizeButtonClick ]);


    useEffect(() => {
        (async () => {
            setSetTopBox(await getSetTopBox(setTopBoxId));
        })();
    }, [ setTopBoxId ]);


    return setTopBox ? (
        <>
            <BackToHome />
            <EntityDescriptionTable>
                <tr><td>이름:</td><td>{ setTopBox.name }</td></tr>
                <tr><td>데이터베이스 ID:</td><td>{ setTopBox.id }</td></tr>
            </EntityDescriptionTable>
            <EntityTable<ProductCategory>
                tableHeadColumn={ getTableHeadColumn }
                getEntityCount={ getCategoriesCount }
                getEntitiesPage={ getCategoriesPage }
                entityToJSX={ entityToJSX }
            />
        </>
    ) : <>Loading...</>
}

export default SetTopBoxMenu