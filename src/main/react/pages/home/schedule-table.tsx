import React, { useCallback, useEffect, useState } from 'react'
import { getAllCategories, ProductCategory } from '../../api/categories';
import { BounceRateCalculation, deleteDefaultStreamSchedule, getTable, ScheduleTable, setDefaultStreamSchedule, TimeSlotBounceRate } from '../../api/scheduleTable';
import { setCategoryList } from '../../redux/categoryListSlice';
import { useAppDispatch, useAppSelector } from '../../redux/hooks';

import './schedule-table.scss'


type DropDownProps = {
    callback: (id: number | null) => void;
    initialId: number | null;
}
const ProductCategoryDropDown : React.FC<DropDownProps> = ({ callback, initialId }) => {

    const [ options, setOptions ] = useState<JSX.Element[]>();
    const categoryListState = useAppSelector(state => state.categoryList);

    useEffect(() => {
        if(!categoryListState.list) return;

        let optionList : JSX.Element[] = [];
        optionList.push(<option key={ 'null' } value={ 'null' }> - </option>)
        for(let category of categoryListState.list) {
            optionList.push(<option key={ category.id } value={ category.id }>{ category.name }</option>)
        }
        setOptions(optionList);
    }, [ categoryListState ])

    return (
        <select 
            className='dropdown' 
            onChange={ (e) => {
                let { value } = e.target;
                callback(value === 'null' ? null : parseInt(value))
            } }
            value={ initialId ?? 'null' }
        >
            { options }
        </select>
    )
}

type ProductCategoryMap = { [ x: number ]: ProductCategory };


const timeSlotName = [ '시간 1', '시간 2', '시간 3', '시간 4', '시간 5', '시간 6' ];
const MainPageScheduleTable : React.FC = () => {

    const [ tableData, setTableData ] = useState<ScheduleTable>();
    const [ categoryMap, setCategoryMap ] = useState<ProductCategoryMap>();

    // redux stuff
    const categoryListState = useAppSelector(state => state.categoryList);
    const dispatch = useAppDispatch();


    useEffect(() => { (async () => {
        if(categoryListState.list) return;
        let list = await getAllCategories();
        dispatch(setCategoryList(list));

        let map: ProductCategoryMap = {};
        for(let category of list) {
            map[category.id] = category;
        }
        setCategoryMap(map);
    })() }, [ categoryListState, dispatch ]);


    useEffect(() => { (async () => {
        if(tableData) return;
        setTableData(await getTable());
    })() }, [ tableData ]);


    const setDefaultStream = useCallback((timeSlotId: number, categoryId: number | null) => {
        if(!tableData) return;
        const newTableData = {
            table: tableData.table.map((e, i) => {
                if(i !== timeSlotId) return e;
                else return [ categoryId, ...e.slice(1) ]
            })
        }
        if(categoryId !== null) setDefaultStreamSchedule(timeSlotId, categoryId);
        else deleteDefaultStreamSchedule(timeSlotId);
        setTableData(newTableData);
    }, [ tableData ]);


    const getDefaultStreamList = useCallback(() => {
        if(!tableData || !categoryMap) return [ <></> ];

        return timeSlotName.map((e, i) => <td key={ `default-${e}` }>
            <ProductCategoryDropDown
                callback={ (id) => setDefaultStream(i, id) }
                initialId={ tableData?.table[i][0] ?? null }
            />
        </td>)
    }, [ categoryMap, setDefaultStream, tableData ]);


    const getAltStreamList = useCallback((altNumber: number) => {
        if(!tableData || !categoryMap) return [ <></> ];
        return timeSlotName.map((e, i) => {
            let categoryId = tableData.table[i][altNumber];
            return (
                <td key={ `alt-${altNumber}-${e}` }>
                    { categoryId !== null ? categoryMap[categoryId].name : '-' }
                </td>
            );
        })
    }, [ categoryMap, tableData ]);


    const getBounceRateList = useCallback((type: keyof TimeSlotBounceRate) => {
        let calculation: BounceRateCalculation = {
            result: []
        }

        return timeSlotName.map((e, i) => {
            let bounceRateData = calculation.result[i];
            return (
                <td key={ `br-${type}-${e}` }>
                    { bounceRateData?.[type] ?? '-' } %
                </td>
            );
        })
    }, [])


    return (
        <div className='schedule-table-wrapper'>
            <table>
                <thead><tr>
                    <td key='thead-empty'></td>
                    { timeSlotName.map(n => <td key={ n }>{ n }</td>) }
                </tr></thead>
                <tbody>
                    <tr>
                        <td>기본</td>
                        { getDefaultStreamList() }
                    </tr>
                    <tr className='br-exp'>
                        <td>예상 Bounce rate</td>
                        { getBounceRateList('onlyDefault') }
                    </tr>
                    <tr><td>대체 1</td>{ getAltStreamList(1) }</tr>
                    <tr><td>대체 2</td>{ getAltStreamList(2) }</tr>
                    <tr><td>대체 3</td>{ getAltStreamList(3) }</tr>
                    <tr className='br-exp'>
                        <td>예상 Bounce rate</td>
                        { getBounceRateList('withAlt') }
                    </tr>
                </tbody>
            </table>
        </div>
    )
}

export default MainPageScheduleTable